package pe.com.integra.irene.scheduler.repository;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import pe.com.integra.irene.scheduler.model.SessionDetail;

import java.util.Map;

@ApplicationScoped
public class SessionRepository {
    private ReactiveKeyCommands<String> keyCommands;
    private final ReactiveValueCommands<String, SessionDetail> values;

    public SessionRepository( ReactiveRedisDataSource reactive) {
        keyCommands = reactive.key();
        this.values = reactive.value(SessionDetail.class);
    }

    public Multi<SessionDetail> getAllSessions() {
        return keyCommands.keys("session:*")
                .onItem().transformToMulti(keys -> Multi.createFrom().items(keys.stream()))
                .onItem().transformToUniAndConcatenate(values::get);
    }

    public Uni<Void> updateSession(SessionDetail sessionDetail, Map<String, Object> metadata) {
        sessionDetail.setMetadata(metadata);
        int expirationTime = 60;
        return values.setex(sessionDetail.getId(), expirationTime, sessionDetail);
    }

    public Uni<Void> deleteSession(String sessionId) {
        return keyCommands.del(sessionId)
                .replaceWithVoid();
    }
}