package cs225;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class ElectionTest {

    @Test
    public void testSingleCandidateWinsAutomatically() {
        Election election = new Election(1);
        election.addCandidate("Nina");
        List<String> winner = election.selectWinner();
        assertEquals(Arrays.asList("Nina"), winner);
    }

    @Test
    public void testMajorityWinnerFirstRound() {
        Election election = new Election(3);
        election.addCandidate("Math");
        election.addCandidate("English");
        election.addCandidate("Chinese");

        election.addBallot(new int[] { 1, 2, 3 }); // Math first choice
        election.addBallot(new int[] { 1, 3, 2 }); // Math first choice
        election.addBallot(new int[] { 2, 1, 3 }); // English first choice
        election.addBallot(new int[] { 2, 3, 1 }); // English first choice
        election.addBallot(new int[] { 3, 1, 2 }); // Chinese first choice
        election.addBallot(new int[] { 1, 2, 3 }); // Additional ballot for Math

        List<String> winner = election.selectWinner();
        assertEquals(Arrays.asList("Math"), winner);
    }

    @Test
    public void testMajorityWinnerFirstRound1() {
        Election election = new Election(4);
        election.addCandidate("Nina");
        election.addCandidate("Gao");
        election.addCandidate("Molly");
        election.addCandidate("Flora");

        election.addBallot(new int[] { 1, 2, 3, 4 }); // Nina first choice
        election.addBallot(new int[] { 1, 3, 2, 4 }); // Nina first choice
        election.addBallot(new int[] { 3, 1, 4, 2 }); // Gao first choice
        election.addBallot(new int[] { 4, 2, 1, 3 }); // Gao first choice
        election.addBallot(new int[] { 4, 1, 3, 2 }); // Molly first choice
        election.addBallot(new int[] { 1, 2, 3, 4 }); // Additional ballot for Nina

        List<String> winner = election.selectWinner();
        assertEquals(Arrays.asList("Nina"), winner);
    }

    @Test
    public void testEliminateLowestCandidate() {
        Election election = new Election(3);
        election.addCandidate("Nina");
        election.addCandidate("Gao");
        election.addCandidate("Molly");

        election.addBallot(new int[] { 1, 2, 3 }); // Nina first choice
        election.addBallot(new int[] { 2, 3, 1 }); // Gao first choice
        election.addBallot(new int[] { 3, 1, 2 }); // Molly first choice
        election.addBallot(new int[] { 3, 2, 1 }); // Molly first choice
        election.addBallot(new int[] { 2, 1, 3 }); // Additional vote for Gao

        List<String> winner = election.selectWinner();
        assertTrue(winner.contains("Molly") && winner.contains("Gao")); //

    }

    @Test
    public void testMultipleRoundsElimination() {
        Election election = new Election(4);
        election.addCandidate("Nina");
        election.addCandidate("Gao");
        election.addCandidate("Molly");
        election.addCandidate("Flora");

        election.addBallot(new int[] { 1, 2, 3, 4 });
        election.addBallot(new int[] { 2, 1, 3, 4 });
        election.addBallot(new int[] { 3, 4, 1, 2 });
        election.addBallot(new int[] { 4, 3, 2, 1 });
        election.addBallot(new int[] { 1, 3, 4, 2 });
        election.addBallot(new int[] { 1, 2, 3, 4 });
        election.addBallot(new int[] { 2, 1, 3, 4 });
        election.addBallot(new int[] { 3, 4, 1, 2 });
        election.addBallot(new int[] { 1, 4, 3, 2 });

        List<String> winner = election.selectWinner();
        assertEquals(Arrays.asList("Nina"), winner);
    }

    @Test
    public void testTieBetweenFinalCandidates() {
        Election election = new Election(2);
        election.addCandidate("Nina");
        election.addCandidate("Gao");

        election.addBallot(new int[] { 1, 2 });
        election.addBallot(new int[] { 2, 1 });

        List<String> winner = election.selectWinner();
        assertEquals(Arrays.asList("Nina", "Gao"), winner);
    }

    @Test
    public void testTieElimination() {
        Election election = new Election(5);
        election.addCandidate("Math");
        election.addCandidate("English");
        election.addCandidate("Chinese");
        election.addCandidate("Music");
        election.addCandidate("Economic");

        election.addBallot(new int[] { 1, 2, 3, 4, 5 });
        election.addBallot(new int[] { 2, 3, 1, 4, 5 });
        election.addBallot(new int[] { 3, 1, 2, 4, 5 });
        election.addBallot(new int[] { 4, 3, 2, 1, 5 });
        election.addBallot(new int[] { 1, 3, 2, 4, 5 });

        List<String> winner = election.selectWinner();
        assertTrue(winner.contains("Math") || winner.contains("English") || winner.contains("Chinese")
                || winner.contains("Music") || winner.contains("Economic"));
    }

    @Test
    public void testRunoffWithFiveCandidates() {
        Election election = new Election(5);
        election.addCandidate("Math");
        election.addCandidate("English");
        election.addCandidate("Chinese");
        election.addCandidate("Music");
        election.addCandidate("Economic");

        election.addBallot(new int[] { 1, 2, 3, 4, 5 });
        election.addBallot(new int[] { 2, 1, 3, 4, 5 });
        election.addBallot(new int[] { 3, 4, 1, 2, 5 });
        election.addBallot(new int[] { 4, 3, 2, 1, 5 });
        election.addBallot(new int[] { 5, 3, 2, 4, 1 });

        List<String> winner = election.selectWinner();
        assertFalse(winner.isEmpty());
    }
}
