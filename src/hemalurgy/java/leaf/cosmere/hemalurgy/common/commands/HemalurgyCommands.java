/*
 * File updated ~ 10 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import leaf.cosmere.common.Cosmere;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import leaf.cosmere.hemalurgy.common.commands.subcommands.HemalurgyVisionCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HemalurgyCommands
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal(Cosmere.MODID)
				.then(Commands.literal(Hemalurgy.MODID)
						.then(HemalurgyVisionCommand.register(dispatcher))));
	}
}
