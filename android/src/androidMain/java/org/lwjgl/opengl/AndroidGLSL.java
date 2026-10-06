package org.lwjgl.opengl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runtime desktop-GLSL to GLSL-ES adaptation for upstream 117HD shaders.
 *
 * Keeping this at shader upload means the Android app can consume upstream 117HD
 * Plugin Hub updates without maintaining a forked shader asset tree.
 */
final class AndroidGLSL
{
	private static final Pattern VERSION = Pattern.compile("(?m)^\\s*#version\\s+\\d+(?:\\s+core)?\\s*$");

	private AndroidGLSL() {}

	static String translate(int shaderType, String source)
	{
		if (source == null) return null;

		String version = shaderType == AndroidGL.GL_COMPUTE_SHADER ? "#version 310 es" : "#version 320 es";
		Matcher matcher = VERSION.matcher(source);
		String translated = matcher.find()
			? matcher.replaceFirst(Matcher.quoteReplacement(version))
			: version + "\n" + source;

		int newline = translated.indexOf('\n');
		String precision =
			"\nprecision highp float;\n" +
			"precision highp int;\n" +
			"precision highp sampler2D;\n" +
			"precision highp sampler2DArray;\n" +
			"precision highp isamplerBuffer;\n" +
			"precision highp samplerBuffer;\n";
		translated = translated.substring(0, newline + 1) + precision + translated.substring(newline + 1);

		return translated
			.replace("#extension GL_ARB_shader_image_load_store : enable", "")
			.replace("#extension GL_EXT_shader_image_load_store : enable", "");
	}
}
