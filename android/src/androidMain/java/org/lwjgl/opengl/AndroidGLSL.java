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
			// Desktop GLSL does not require default sampler/image precision, while
			// GLSL ES does for types without a built-in default. Keep the complete
			// set used by the pinned upstream 117HD shaders here so includes can
			// remain byte-for-byte upstream.
			"precision highp sampler2D;\n" +
			"precision highp sampler2DArray;\n" +
			"precision highp sampler2DShadow;\n" +
			"precision highp samplerCube;\n" +
			"precision highp sampler3D;\n" +
			"precision highp isampler3D;\n" +
			"precision highp usampler2DArray;\n" +
			"precision highp isamplerBuffer;\n" +
			"precision highp samplerBuffer;\n" +
			"precision highp uimage2DArray;\n";
		translated = translated.substring(0, newline + 1) + precision + translated.substring(newline + 1);

		return translated
			.replace("#extension GL_ARB_shader_image_load_store : enable", "")
			.replace("#extension GL_EXT_shader_image_load_store : enable", "");
	}
}
