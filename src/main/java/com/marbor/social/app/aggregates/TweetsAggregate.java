package com.marbor.social.app.aggregates;


import com.marbor.social.app.commands.CreateTweetCommand;
import com.marbor.social.app.events.TweetCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


public class TweetsAggregate
{
    @AggregateIdentifier
    private String id;

    public TweetsAggregate() {
    }

    @CommandHandler
    public TweetsAggregate(CreateTweetCommand command) {
        apply(new TweetCreatedEvent(command.getId(), command.getText(), command.getAuthorId()));
    }

    @EventHandler
    public void on(TweetCreatedEvent event) {
        this.id = event.getId();
    }
}