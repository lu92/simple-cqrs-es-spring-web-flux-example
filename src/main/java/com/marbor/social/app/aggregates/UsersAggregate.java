package com.marbor.social.app.aggregates;


import com.marbor.social.app.commands.user.SubscribeCommand;
import com.marbor.social.app.commands.user.CreateUserCommand;
import com.marbor.social.app.events.user.SubscribeEvent;
import com.marbor.social.app.events.user.UserCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


public class UsersAggregate
{
    @AggregateIdentifier
    private String id;

    public UsersAggregate() {
    }

    @CommandHandler
    public UsersAggregate(CreateUserCommand command) {
        apply(new UserCreatedEvent(command.getId(), command.getName()));
    }

    @CommandHandler
    public void addFollower(SubscribeCommand command) {
        apply(new SubscribeEvent(command.getId(), command.getFollowedId()));
    }

    @EventHandler
    public void on(UserCreatedEvent event) {
        this.id = event.getId();
    }
}