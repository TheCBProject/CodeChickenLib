package codechicken.lib.render.shader;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.covers1624.quack.io.IOUtils;
import net.covers1624.quack.sort.TopologicalSort;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Similar to Mojang's own {@link GlslPreprocessor}.
 * However, more suited to a modded environment.
 * <p>
 * Created by covers1624 on 22/3/22.
 */
@SuppressWarnings ("ALL")
public class GlslProcessor {

    private static Logger LOGGER = LogManager.getLogger();

    private static final boolean DEBUG = Boolean.getBoolean("ccl.glsl_processor.debug");

    private static final Pattern VERSION_PATTERN = Pattern.compile("^#version (.*)$");
    // Matches #moj_import with <> or ""
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^#moj_import (?>(?><(.*)>)|(?>\"(.*)\"))$");

    private final ResourceProvider resourceProvider;
    private final ResourceLocation shader;

    private final Map<ResourceLocation, ProcessorEntry> processorLookup = new HashMap<>();
    private final LinkedList<ProcessorEntry> newProcessors = new LinkedList<>();

    public GlslProcessor(ResourceLocation shader) {
        this(Minecraft.getInstance().getResourceManager(), shader);
    }

    public GlslProcessor(ResourceProvider resourceProvider, ResourceLocation shader) {
        this.resourceProvider = resourceProvider;
        this.shader = shader;
        newProcessors.add(processorLookup.computeIfAbsent(shader, ProcessorEntry::new));
    }

    public ProcessedShader process() {
        ProcessorEntry mainEntry = newProcessors.peek();
        String mainVersion = Objects.requireNonNull(mainEntry.version, "Main Shader '" + shader + "' in chain requires #version.");

        if (mainEntry.includes.isEmpty()) {
            return new ProcessedShader(
                    shader,
                    mainEntry.sourceName,
                    List.of(shader),
                    String.join("\n", mainEntry.lines)
            );
        }

        MutableGraph<ResourceLocation> graph = GraphBuilder.directed().build();
        while (!newProcessors.isEmpty()) {
            ProcessorEntry entry = newProcessors.pop();
            if (entry.version != null && !entry.version.equals(mainVersion)) {
                LOGGER.warn("Shader chain {} -> {} version discrepency. Main Shader: {}, Included Shader: {}. This shader may not compile.", shader, entry.shader, mainVersion, entry.version);
            }
            for (ResourceLocation include : entry.includes) {
                graph.putEdge(include, entry.shader);
                if (!processorLookup.containsKey(include)) {
                    newProcessors.add(processorLookup.computeIfAbsent(include, ProcessorEntry::new));
                }
            }
        }

        List<ResourceLocation> order = TopologicalSort.topologicalSort(graph, null);
        List<String> outputLines = new LinkedList<>();
        outputLines.add("#version " + mainVersion);
        for (ResourceLocation resourceLocation : order) {
            outputLines.add("");
            outputLines.add("/*" + resourceLocation + "*/");
            ProcessorEntry entry = processorLookup.get(resourceLocation);
            for (int i = 0; i < entry.lines.size(); i++) {
                String line = entry.lines.get(i);
                if (entry.linesToComment.contains(i)) {
                    outputLines.add("/*" + line + "*/");
                } else {
                    outputLines.add(line);
                }
            }
        }

        if (DEBUG) {
            Path out = Paths.get("glsl")
                    .resolve("assets")
                    .resolve(shader.getNamespace())
                    .resolve(shader.getPath());
            try {
                Files.write(IOUtils.makeParents(out), outputLines, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                LOGGER.error("Failed to write debug glsl output to {}", out);
            }
        }

        return new ProcessedShader(
                shader,
                mainEntry.sourceName,
                List.copyOf(order),
                String.join("\n", outputLines)
        );
    }

    public static record ProcessedShader(
            ResourceLocation shader,
            String sourceName,
            List<ResourceLocation> order,
            String processedSource) {
    }

    private class ProcessorEntry {

        private final ResourceLocation shader;
        private final String sourceName;
        private final List<String> lines;
        private final List<ResourceLocation> includes;
        @Nullable
        private final String version;
        private final IntSet linesToComment;

        private ProcessorEntry(ResourceLocation shader) {
            this.shader = shader;
            try {
                Resource resource = resourceProvider.getResourceOrThrow(shader);
                sourceName = resource.sourcePackId();
                try (BufferedReader reader = resource.openAsReader()) {
                    lines = reader.lines().toList();
                }
            } catch (IOException ex) {
                throw new RuntimeException("Unable to read asset '" + shader + "'.", ex);
            }
            linesToComment = new IntOpenHashSet();
            includes = extractIncludes(lines, linesToComment);
            version = extractVersion(lines, linesToComment);
        }

        private static List<ResourceLocation> extractIncludes(List<String> lines, IntSet linesToComment) {
            List<ResourceLocation> imports = new LinkedList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Matcher matcher = IMPORT_PATTERN.matcher(line);
                if (matcher.find()) {
                    linesToComment.add(i);
                    String match = matcher.group(1);
                    boolean includeFolder = match != null;
                    if (!includeFolder) {
                        match = matcher.group(2);
                    }
                    ResourceLocation loc = new ResourceLocation(match);
                    if (includeFolder) {
                        loc = new ResourceLocation(loc.getNamespace(), FilenameUtils.normalize("shaders/include/" + loc.getPath(), true));
                    }
                    imports.add(loc);
                }
            }

            return List.copyOf(imports);
        }

        @Nullable
        private static String extractVersion(List<String> lines, IntSet linesToComment) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Matcher matcher = VERSION_PATTERN.matcher(line);
                if (matcher.find()) {
                    linesToComment.add(i);
                    return matcher.group(1);
                }
            }
            return null;
        }
    }

}
