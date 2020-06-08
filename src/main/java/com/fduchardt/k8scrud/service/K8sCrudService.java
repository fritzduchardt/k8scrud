package com.fduchardt.k8scrud.service;

import com.fduchardt.k8scrud.exception.*;
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

    private static final String K8S_CRUD_ID = "\\{\\{ .K8scrud.id \\}\\}";
    private static final String K8S_CRUD_PARAM_PREFIX = "\\{\\{ .K8scrud.params.%s \\}\\}";

    @Value("${k8scrud.yamldir}")
    private String yamlDir;

    public K8sResponseDto replace(String name, String k8sCrudId, Map<String, String> params) throws IOException, InterruptedException {
        return execute(name, SupportedK8sCommand.REPLACE, k8sCrudId, params);
    }

    public K8sResponseDto create(String name, Map<String, String> params) throws IOException, InterruptedException {
        return execute(name, SupportedK8sCommand.CREATE, generateK8sCrudId(), params);
    }

    public K8sResponseDto apply(String name, String k8sCrudId, Map<String, String> params) throws IOException, InterruptedException {
        return execute(name, SupportedK8sCommand.APPLY, k8sCrudId, params);
    }

    public K8sResponseDto delete(String name, String k8sCrudId, Map<String, String> params) throws IOException, InterruptedException {
        return execute(name, SupportedK8sCommand.DELETE, k8sCrudId, params);
    }

    private K8sResponseDto execute(String name, SupportedK8sCommand k8sCommand, String k8sCrudId, Map<String, String> params) throws IOException, InterruptedException {

        String yaml = loadYaml(name, k8sCrudId, params);

        String[] command = buildCommand(k8sCommand, yaml);

        log.debug("Execute command:\n{}", Stream.of(command).collect(Collectors.joining(" ")));

        Process process = executeCommand(command);

        process.waitFor();

        assertError(command, process, k8sCrudId);

        Optional<String> answer = readAnswer(process);

        if (answer.isEmpty()) {
            throw new K8sCrudException(k8sCrudId, "No response", command);
        }

        return new K8sResponseDto(answer.get(), k8sCrudId, command);
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

    private String loadYaml(String yamlName, String k8sCrudId, Map<String, String> params) {
        String yaml;
        try {
            yaml = Files.readString(Path.of(String.format("%s/%s.yaml", yamlDir, yamlName)));
        } catch (IOException e) {
            throw new K8sCrudException(k8sCrudId, "Manifest not found: " + yamlName);
        }
        yaml = yaml.replaceAll(K8S_CRUD_ID, k8sCrudId);
        if (params != null) {
            for (String key : params.keySet()) {
                yaml = yaml.replaceAll(String.format(K8S_CRUD_PARAM_PREFIX, key), params.get(key));
            }
        }
        return yaml;
    }

    private String generateK8sCrudId() {
        String k8sCrudId = UUID.randomUUID().toString();
        log.info("K8sCrudId: {}", k8sCrudId);
        return k8sCrudId;
    }

    private void assertError(String[] command, Process p, String k8sCrudId) throws IOException {
        try (InputStream is = p.getErrorStream()) {
            if (is != null && is.available() > 0) {
                throw new K8sCrudException(k8sCrudId, IOUtils.toString(is, StandardCharsets.UTF_8), command);
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
