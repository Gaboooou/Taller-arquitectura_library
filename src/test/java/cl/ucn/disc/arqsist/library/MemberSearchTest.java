/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.OrmliteMemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.grpc.MemberClient;
import cl.ucn.disc.arqsist.library.grpc.MemberGrpcService;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.MemberService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Acceptance tests for the SearchMembers gRPC RPC.
 */
final class MemberSearchTest {

    private Server server;
    private MemberClient client;

    /**
     * Start an isolated in-memory database and in-process gRPC server before each test,
     * registering the three acceptance-case members in the required order.
     *
     * @throws Exception if the server fails to start
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        MemberService memberService = new MemberService(new OrmliteMemberDao(db.connectionSource()));

        String serverName = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(serverName).directExecutor()
                .addService(new MemberGrpcService(memberService))
                .build()
                .start();
        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        client = new MemberClient(channel);

        // Register the three members in the required order.
        client.register(new Member("Ada Lovelace", "ada@example.com"));
        client.register(new Member("Grace Hopper", "grace@example.com"));
        client.register(new Member("Alan Turing", "alan@example.com"));
    }

    /**
     * Close the client channel and stop the in-process server after each test.
     */
    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void case1_matchesLowercaseSubstring() {
        List<Member> result = client.search("ada");
        assertEquals(1, result.size());
        assertEquals("Ada Lovelace", result.getFirst().getName());
    }

    @Test
    void case2_matchesCaseInsensitive() {
        List<Member> result = client.search("ADA");
        assertEquals(1, result.size());
        assertEquals("Ada Lovelace", result.getFirst().getName());
    }

    @Test
    void case3_trimsWhitespaceAroundQuery() {
        List<Member> result = client.search("  ada  ");
        assertEquals(1, result.size());
        assertEquals("Ada Lovelace", result.getFirst().getName());
    }

    @Test
    void case4_returnsMultipleMatchesOrderedByIdAscending() {
        List<Member> result = client.search("a");
        List<String> names = result.stream().map(Member::getName).toList();
        assertEquals(List.of("Ada Lovelace", "Grace Hopper", "Alan Turing"), names);
        assertTrue(result.get(0).getId() < result.get(1).getId());
        assertTrue(result.get(1).getId() < result.get(2).getId());
    }

    @Test
    void case5_noMatchReturnsEmptyListWithOkStatus() {
        List<Member> result = client.search("zzz");
        assertTrue(result.isEmpty());
    }

    @Test
    void case6_percentAndUnderscoreAreLiteralCharacters() {
        assertTrue(client.search("%").isEmpty());
        assertTrue(client.search("_").isEmpty());
    }

    @Test
    void case7_blankQueryThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> client.search(""));
        assertThrows(IllegalArgumentException.class, () -> client.search("   "));
    }
}
