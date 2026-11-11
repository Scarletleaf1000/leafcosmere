/*
 * File updated ~ 10 - 11 - 2026 ~ Leaf
 */

package leaf.cosmere.hemalurgy.common.commands.subcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import leaf.cosmere.hemalurgy.common.HemalurgyVision;
import leaf.cosmere.hemalurgy.common.network.packets.VisionStateMessage;
import leaf.cosmere.hemalurgy.common.Hemalurgy;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class HemalurgyVisionCommand
{
	private static final SimpleCommandExceptionType INVALID_STATE = new SimpleCommandExceptionType(Component.literal("Invalid state, expected on or off"));

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		return Commands.literal("vision")
				.requires(context -> context.hasPermission(2))
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("state", StringArgumentType.word())
								.suggests((context, builder) -> SharedSuggestionProvider.suggest(new String[]{"on", "off"}, builder))
								.executes(HemalurgyVisionCommand::setVision)));
	}

	private static int setVision(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String stateArg = StringArgumentType.getString(context, "state");
		boolean value;
		Component stateWord;
		if (stateArg.equalsIgnoreCase("on"))
		{
			value = true;
			stateWord = Component.translatable("state.hemalurgy.on");
		}
		else if (stateArg.equalsIgnoreCase("off"))
		{
			value = false;
			stateWord = Component.translatable("state.hemalurgy.off");
		}
		else
		{
			throw INVALID_STATE.create();
		}

		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
		CommandSourceStack source = context.getSource();
		for (ServerPlayer target : targets)
		{
			HemalurgyVision.setEnabled(target, value);
			Hemalurgy.packetHandler().sendTo(new VisionStateMessage(value), target);
			target.sendSystemMessage(Component.translatable("command.hemalurgy.vision.notify", stateWord));
			source.sendSuccess(() -> Component.translatable("command.hemalurgy.vision.set", target.getName(), stateWord), true);
		}
		return targets.size();
	}
}
