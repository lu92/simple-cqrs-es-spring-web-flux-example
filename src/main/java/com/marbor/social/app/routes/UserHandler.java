package com.marbor.social.app.routes;

import com.marbor.social.app.commands.user.AddFollowerCommand;
import com.marbor.social.app.commands.user.CreateUserCommand;
import com.marbor.social.app.domain.User;
import com.marbor.social.app.repositories.UserRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.marbor.social.app.routes.RestMessages.USER_ALREADY_EXISTS;
import static com.marbor.social.app.utils.Utils.toMono;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * Created by marcin on 07.07.17.
 */
class UserHandler
{
    private final CommandGateway commandGateway;

    UserHandler(CommandGateway commandGateway)
    {
        this.commandGateway = commandGateway;
    }


    Mono<ServerResponse> createUser(ServerRequest request)
    {
        Mono<User> user = request.body(BodyExtractors.toMono(User.class));
        Mono<ServerResponse> badRequest = ServerResponse.badRequest()
                .body(fromObject(USER_ALREADY_EXISTS.message()));

        return user.flatMap(u ->
                UserRepository.getRepository()
                        .containsUserWithName(u.getName())
                        .flatMap(userExists ->
                        {
                            if (userExists)
                            {
                                return badRequest;
                            }
                            else
                            {
                                return toMono(commandGateway.send(new CreateUserCommand(u.getId(), u.getName())))
                                        .flatMap(result -> ServerResponse.ok()
                                                .contentType(APPLICATION_JSON).body(fromObject(u)));
                            }
                        }));

    }

    Mono<ServerResponse> getUser(ServerRequest request)
    {
        String id = request.pathVariable("id");
        Mono<User> user = UserRepository.getRepository().findById(id);

        Mono<ServerResponse> notFound = ServerResponse
                .notFound()
                .header("Message", "User not found")
                .build();

        return user.flatMap(u -> ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(fromObject(u))
                .switchIfEmpty(notFound));
    }

    Mono<ServerResponse> getUsers(ServerRequest request)
    {
        Mono<List<User>> users = UserRepository.getRepository().findAll();
        Mono<ServerResponse> notFound = ServerResponse
                .notFound()
                .header("Message", "Users not found")
                .build();

        return users.flatMap(u -> ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(fromObject(u))
                .switchIfEmpty(notFound));
    }

    Mono<ServerResponse> addFollower(ServerRequest request)
    {
        String id = request.pathVariable("id");
        String followerId = request.pathVariable("followerId");

        //TODO follower already exists scenario
        return toMono(commandGateway.send(new AddFollowerCommand(id, followerId)))
                .flatMap((result) -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(fromObject("Follower: " + followerId + " added to: " + id)));
    }

    Mono<ServerResponse> getFollowers(ServerRequest request)
    {
        Mono<User> user = UserRepository.getRepository().findById(request.pathVariable("id"));
        Mono<ServerResponse> notFound = ServerResponse
                .notFound()
                .header("Message", "Users not found")
                .build();

        return user.flatMap(u -> ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(fromObject(u.getFollowers()))
                .switchIfEmpty(notFound));
    }


}