package com.fduchardt.k8scrud.service;

import lombok.extern.slf4j.*;
import org.apache.commons.io.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

@Component
@Slf4j
public class K8sCrudService {

    private static final String K8S_CRUD_ID = "\\{\\{ K8sCRUD.id \\}\\}";

    @Value("${k8scrud.yamldir}")
    private String yamlDir;

    public String apply(String name) throws IOException, InterruptedException {
        return execute(name, SupportedK8sCommand.APPLY, generateK8sCrudId());
    }

    public String delete(String name, String k8sCrudId) throws IOException, InterruptedException {
        return execute(name, SupportedK8sCommand.APPLY, k8sCrudId);
    }

    private String execute(String name, SupportedK8sCommand k8sCommand, String k8sCrudId) throws IOException, InterruptedException {

        String yaml = loadYaml(name, k8sCrudId);

        String[] command = buildCommand(k8sCommand, yaml);

        log.debug("Execute command:\n{}", Stream.of(command).collect(Collectors.joining(" ")));

        Process p = executeCommand(command);

        p.waitFor();

        assertError(command, p);

        Optional<String> answer = readAnswer(p);

        if (answer.isEmpty()) {
            throw new RuntimeException("No response for command:\n" + command);
        }

        return answer.get();
    }

    private Process executeCommand(String[] command) throws IOException {
        log.info("Execute command:\n{}", String.join(" ", command));
        return Runtime.getRuntime().exec(command);
    }

    private Optional<String> readAnswer(Process process) throws IOException {
        try (InputStream is = process.getInputStream()) {
            if (is != null && is.available() > 0) {
                return Optional.of(IOUtils.toString(is, StandardCharsets.UTF_8));
            }
        }
        return Optional.empty();
    }

    private String loadYaml(String yamlName, String s) throws IOException {
        String yaml = Files.readString(Path.of(String.format("%s/%s.yaml", yamlDir, yamlName)));
        return yaml.replaceAll(K8S_CRUD_ID, s);
    }

    private String generateK8sCrudId() {
        String k8sCrudId = UUID.randomUUID().toString();
        log.info("K8sCrudId: {}", k8sCrudId);
        return k8sCrudId;
    }

    private void assertError(String[] command, Process p) throws IOException {
        try (InputStream is = p.getErrorStream()) {
            if (is != null && is.available() > 0) {
                throw new RuntimeException("Error during execution: " + IOUtils.toString(is, StandardCharsets.UTF_8) + " on command:\n" + command);
            }
        }
    }

    private String[] buildCommand(SupportedK8sCommand command, String yaml) {
        return new String[]{
                "/bin/sh",
                "-c",
                String.format("cat << EOF | %s\n%s\nEOF", command.getCommand(), yaml)
        };
    }
}
