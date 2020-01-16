package ninja.bytecode.iris.generator.genobject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import net.md_5.bungee.api.ChatColor;
import ninja.bytecode.iris.Iris;
import ninja.bytecode.iris.controller.PackController;
import ninja.bytecode.iris.util.Direction;
import ninja.bytecode.shuriken.collections.GList;
import ninja.bytecode.shuriken.execution.TaskExecutor;
import ninja.bytecode.shuriken.execution.TaskExecutor.TaskGroup;
import ninja.bytecode.shuriken.format.F;
import ninja.bytecode.shuriken.io.IO;
import ninja.bytecode.shuriken.logging.L;

public class GenObjectGroup
{
	private GList<GenObject> schematics;
	private GList<String> flags;
	private String name;

	public GenObjectGroup(String name)
	{
		this.schematics = new GList<>();
		this.flags = new GList<>();
		this.name = name;
	}

	public void read(DataInputStream din) throws IOException
	{
		flags.clear();
		schematics.clear();
		name = din.readUTF();
		int fl = din.readInt();
		int sc = din.readInt();

		for(int i = 0; i < fl; i++)
		{
			flags.add(din.readUTF());
		}

		for(int i = 0; i < sc; i++)
		{
			GenObject g = new GenObject(0, 0, 0);
			g.readDirect(din);
			schematics.add(g);
		}
	}

	public void write(DataOutputStream dos, Consumer<Double> progress) throws IOException
	{
		dos.writeUTF(name);
		dos.writeInt(flags.size());
		dos.writeInt(schematics.size());

		for(String i : flags)
		{
			dos.writeUTF(i);
		}

		int of = 0;

		if(progress != null)
		{
			progress.accept((double) of / (double) schematics.size());
		}

		for(GenObject i : schematics)
		{
			i.writeDirect(dos);
			of++;

			if(progress != null)
			{
				progress.accept((double) of / (double) schematics.size());
			}
		}
	}

	public void applySnowFilter(int factor)
	{
		if(flags.contains("no snow"))
		{
			L.i(ChatColor.DARK_AQUA + "Skipping Snow Filter for " + ChatColor.GRAY + getName());
			return;
		}

		L.i(ChatColor.AQUA + "Applying Snow Filter to " + ChatColor.WHITE + getName());

		for(GenObject i : schematics)
		{
			i.applySnowFilter(factor);
		}
	}

	public GenObjectGroup copy(String suffix)
	{
		GenObjectGroup gog = new GenObjectGroup(name + suffix);
		gog.schematics = new GList<>();
		gog.flags = flags.copy();

		for(GenObject i : schematics)
		{
			GenObject g = i.copy();
			g.setName(i.getName() + suffix);
			gog.schematics.add(g);
		}

		return gog;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public GList<GenObject> getSchematics()
	{
		return schematics;
	}

	public void setSchematics(GList<GenObject> schematics)
	{
		this.schematics = schematics;
	}

	public GList<String> getFlags()
	{
		return flags;
	}

	public void setFlags(GList<String> flags)
	{
		this.flags = flags;
	}

	public int size()
	{
		return getSchematics().size();
	}

	public static GenObjectGroup load(String string)
	{
		File folder = Iris.getController(PackController.class).loadFolder(string);

		if(folder != null)
		{
			GenObjectGroup g = new GenObjectGroup(string);

			for(File i : folder.listFiles())
			{
				if(i.getName().endsWith(".ifl"))
				{
					try
					{
						g.flags.add(IO.readAll(i).split("\\Q\n\\E"));
					}

					catch(IOException e)
					{
						L.ex(e);
					}
				}

				if(i.getName().endsWith(".ish"))
				{
					try
					{
						GenObject s = GenObject.load(i);
						g.getSchematics().add(s);
					}

					catch(IOException e)
					{
						L.f("Cannot load Schematic: " + string + "/" + i.getName());
						L.ex(e);
					}
				}
			}

			return g;
		}

		return null;
	}

	public void processVariants()
	{
		GList<GenObject> inject = new GList<>();
		String x = Thread.currentThread().getName();
		ReentrantLock rr = new ReentrantLock();
		TaskExecutor ex = new TaskExecutor(Iris.settings.performance.compilerThreads, Iris.settings.performance.compilerPriority, x + "/Subroutine ");
		TaskGroup gg = ex.startWork();
		for(GenObject i : getSchematics())
		{
			for(Direction j : new Direction[] {Direction.S, Direction.E, Direction.W})
			{
				GenObject cp = i.copy();

				gg.queue(() ->
				{
					GenObject f = cp;
					f.rotate(Direction.N, j);
					rr.lock();
					inject.add(f);
					rr.unlock();
				});
			}
		}

		gg.execute();
		gg = ex.startWork();
		getSchematics().add(inject);

		for(GenObject i : getSchematics())
		{
			gg.queue(() ->
			{
				i.recalculateMountShift();

				for(String j : flags)
				{
					i.computeFlag(j);
				}
			});
		}

		gg.execute();
		ex.close();

		L.i(ChatColor.LIGHT_PURPLE + "Processed " + ChatColor.WHITE + F.f(schematics.size()) + ChatColor.LIGHT_PURPLE + " Schematics in " + ChatColor.WHITE + name);
	}
}