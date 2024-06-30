package org.battleplugins.arena.editor.stage;

import org.battleplugins.arena.editor.EditorContext;
import org.battleplugins.arena.editor.WizardStage;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.util.InteractionInputs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TeamSpawnInputStage<E extends EditorContext<E>> implements WizardStage<E> {
    private final Message chatMessage;
    private final String input;
    private final Predicate<E> doneCondition;
    private final Function<E, BiConsumer<String, Location>> inputConsumer;
    private final Function<E, Consumer<String>> clearConsumer;

    public TeamSpawnInputStage(Message chatMessage, String input, Predicate<E> doneCondition,
                               Function<E, BiConsumer<String, Location>> inputConsumer, Function<E, Consumer<String>> clearConsumer) {
        this.chatMessage = chatMessage;
        this.input = input;
        this.doneCondition = doneCondition;
        this.inputConsumer = inputConsumer;
        this.clearConsumer = clearConsumer;
    }

    @Override
    public void enter(E context) {
        if (this.chatMessage != null) {
            this.chatMessage.send(context.getPlayer());
        }

        this.enterWizardConversation(context);
    }

    private void enterWizardConversation(E context) {
        Player player = context.getPlayer();

        // Player types in chat their location is used for the spawn
        new InteractionInputs.ChatInput(player, null) {

            @Override
            public void onChatInput(String input) {
                // If the user types "done", we are done with the wizard
                if ("done".equalsIgnoreCase(input)) {
                    // Ensure there are valid team spawns
                    if (doneCondition.test(context)) {
                        context.advanceStage();
                    }

                    return;
                }

                if ("cancel".equalsIgnoreCase(input)) {
                    context.getWizard().onCancel(context);
                    return;
                }

                boolean clear = "clear".equalsIgnoreCase(input);

                // We have the location, now we need to get the team name
                Location location = player.getLocation().clone();

                if (clear) {
                    Messages.MAP_CLEAR_TEAM_SPAWN_TEAM.send(player);
                } else {
                    Messages.MAP_ADD_TEAM_SPAWN_TEAM.send(player);
                }

                // Send list of valid teams
                List<String> teamNames = context.getArena().getTeams().getAvailableTeams()
                        .stream()
                        .map(ArenaTeam::getName)
                        .toList();

                Messages.VALID_TEAMS.send(player, String.join(", ", teamNames));

                new InteractionInputs.ChatInput(player, Messages.INVALID_TEAM_VALID_TEAMS.withContext(String.join(", ", teamNames))) {

                    @Override
                    public void onChatInput(String input) {
                        if ("cancel".equalsIgnoreCase(input)) {
                            context.getWizard().onCancel(context);
                            return;
                        }

                        if (clear) {
                            clearConsumer.apply(context).accept(input);

                            Messages.MAP_ADD_TEAM_SPAWN_CLEARED.send(player, input);
                        } else {
                            inputConsumer.apply(context).accept(input, location);

                            Messages.MAP_ADD_TEAM_SPAWN_ADDED.send(player, input);
                        }

                        // Keep re-entering the wizard conversation until the user types "done"
                        enterWizardConversation(context);
                    }

                    @Override
                    public boolean isValidChatInput(String input) {
                        return TextInputStage.isCancel(input) || (!input.startsWith("/") && teamNames.contains(input));
                    }
                };
            }

            @Override
            public boolean isValidChatInput(String input) {
                return input.equalsIgnoreCase("clear") || TextInputStage.isCancelOrDone(input) || (!input.startsWith("/") && TeamSpawnInputStage.this.input.equalsIgnoreCase(input));
            }
        };
    }
}