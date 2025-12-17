package fr.tt54.killTheBrioche.twitch;

import java.util.List;

public record TwitchToken(String access_token, int expires_in, String refresh_token, List<String> scope, String token_type) {
}
