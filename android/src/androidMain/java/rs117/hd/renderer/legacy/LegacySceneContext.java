package rs117.hd.renderer.legacy;

import net.runelite.api.Client;
import net.runelite.api.Scene;
import rs117.hd.scene.SceneContext;

/**
 * Type marker retained for common 117HD code which distinguishes old and modern
 * scene contexts with instanceof checks. Android never constructs this renderer.
 */
public class LegacySceneContext extends SceneContext
{
	public LegacySceneContext(Client client, Scene scene, int expandedMapLoadingChunks)
	{
		super(client, scene, expandedMapLoadingChunks);
	}
}
