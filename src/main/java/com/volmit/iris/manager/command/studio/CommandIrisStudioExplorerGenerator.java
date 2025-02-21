package com.volmit.iris.manager.command.studio;

import com.volmit.iris.Iris;
import com.volmit.iris.IrisSettings;
import com.volmit.iris.manager.gui.NoiseExplorer;
import com.volmit.iris.object.IrisGenerator;
import com.volmit.iris.util.KList;
import com.volmit.iris.util.MortarCommand;
import com.volmit.iris.util.MortarSender;
import com.volmit.iris.util.RNG;

public class CommandIrisStudioExplorerGenerator extends MortarCommand
{
	public CommandIrisStudioExplorerGenerator()
	{
		super("generator", "gen", "g");
		setDescription("Explore different generators");
		requiresPermission(Iris.perm.studio);
		setCategory("World");
	}

	@Override
	public void addTabOptions(MortarSender sender, String[] args, KList<String> list) {

	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!IrisSettings.get().isStudio())
		{
			sender.sendMessage("To use Iris Studio, please enable studio in Iris/settings.json");
			return true;
		}

		if(!IrisSettings.get().isUseServerLaunchedGuis())
		{
			sender.sendMessage("To use Iris Guis, please enable serverLaunchedGuis in Iris/settings.json");
			return true;
		}

		if(!Iris.proj.isProjectOpen())
		{
			sender.sendMessage("No project is open");
			return true;
		}

		if(args.length == 0)
		{
			sender.sendMessage("Provide a generator name");
			return true;
		}

		else
		{
			String g = args[0];
			IrisGenerator b = Iris.proj.getActiveProject().getActiveProvider().getData().getGeneratorLoader().load(g);

			if(b != null)
			{
				NoiseExplorer.launch((x, z) ->
				{
					return b.getHeight(x, z, new RNG(Iris.proj.getActiveProject().getActiveProvider().getTarget().getWorld().getSeed()).nextParallelRNG(3245).lmax());
				}, "Gen: " + b.getLoadKey());

				sender.sendMessage("Opening Noise Explorer for gen " + b.getLoadKey());
				return true;
			}

			else
			{
				sender.sendMessage("Invalid Generator");
			}
		}

		return true;
	}

	@Override
	protected String getArgsUsage()
	{
		return "";
	}
}
