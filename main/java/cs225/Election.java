package cs225;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An Election consists of the candidates running for office, the ballots that
 * have been cast, and the total number of voters. This class implements the
 * ranked choice voting algorithm.
 *
 * Ranked choice voting uses this process:
 * <ol>
 * <li>Rather than vote for a single candidate, a voter ranks all the
 * candidates. For example, if 3 candidates are running on the ballot, a voter
 * identifies their first choice, second choice, and third choice.
 * <li>The first-choice votes are tallied. If any candidate receives &gt; 50%
 * of the votes, that candidate wins.
 * <li>If no candidate wins &gt; 50% of the votes, the candidate(s) with the
 * lowest number of votes is(are) eliminated. For each ballot in which an
 * eliminated candidate is the first choice, the 2nd ranked candidate is now
 * the top choice for that ballot.
 * <li>Steps 2 &amp; 3 are repeated until a candidate wins, or all remaining
 * candidates have exactly the same number of votes. In the case of a tie,
 * there would be a separate election involving just the tied candidates.
 * </ol>
 */
public class Election {
    // All candidates that were in the election initially. If a candidate is
    // eliminated, they will still stay in this array.
    private final Candidate[] candidates;
    // The next slot in the candidates array to fill.
    private int nextCandidate;

    /**
     * Create a new Election object. Initially, there are no candidates or
     * votes.
     * 
     * @param numCandidates the number of candidates in the election
     */

    public Election(int numCandidates) {
        this.candidates = new Candidate[numCandidates];
    }

    /**
     * Adds a candidate to the election
     * 
     * @param name the candidate’s name
     */
    public void addCandidate(String name) {
        candidates[nextCandidate] = new Candidate(name);
        nextCandidate++;
    }

    /**
     * Adds a completed ballot to the election.
     * 
     * @param ranks A correctly formulated ballot will have exactly 1
     *              entry with a rank of 1, exactly one entry with a rank of 2, etc.
     *              If there are n candidates on the ballot, the values in the rank
     *              array passed to the constructor will be some permutation of the
     *              numbers 1 to n.
     * @throws IllegalArgumentException if the ballot is not valid.
     */
    public void addBallot(int[] ranks) {
        if (!isBallotValid(ranks)) {
            throw new IllegalArgumentException("Invalid ballot");
        }
        Ballot newBallot = new Ballot(ranks);
        assignBallotToCandidate(newBallot);
    }

    /**
     * Checks that the ballot is the right length and contains a permutation
     * of the numbers 1 to n, where n is the number of candidates.
     * 
     * @param ranks the ballot to check
     * @return true if the ballot is valid.
     */
    private boolean isBallotValid(int[] ranks) {
        if (ranks.length != candidates.length) {
            return false;
        }
        int[] sortedRanks = Arrays.copyOf(ranks, ranks.length);
        Arrays.sort(sortedRanks);
        for (int i = 0; i < sortedRanks.length; i++) {
            if (sortedRanks[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines which candidate is the top choice on the ballot and gives the
     * ballot to that candidate.
     * 
     * @param newBallot a ballot that is not currently assigned to a candidate
     */
    private void assignBallotToCandidate(Ballot newBallot) {
        int candidateId = newBallot.getTopCandidate();
        if (!candidates[candidateId].isEliminated()) {
            candidates[candidateId].addBallot(newBallot);
        }
    }

    /**
     * Calculates the total number of votes for all non-eliminated candidates.
     * 
     * @return the total number of votes currently in the election
     */
    private int getTotalVotes() {
        int total = 0;
        for (Candidate candidate : candidates) {
            if (!candidate.isEliminated()) {
                total += candidate.getVotes();
            }
        }
        return total;
    }

    /**
     * Counts the number of candidates who have not been eliminated.
     * 
     * @return the number of remaining candidates in the election
     */
    private int getRemainingCandidates() {
        int count = 0;
        for (Candidate candidate : candidates) {
            if (!candidate.isEliminated()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Apply the ranked choice voting algorithm to identify the winner.
     * 
     * @return If there is a winner, this method returns a list containing just
     *         the winner’s name is returned. If there is a tie, this method returns
     *         a
     *         list containing the names of the tied candidates.
     */
    public List<String> selectWinner() {
        while (true) {
            // Check if a candidate has majority votes
            for (Candidate candidate : candidates) {
                if (!candidate.isEliminated() && candidate.getVotes() > getTotalVotes() / 2) {
                    List<String> winner = new ArrayList<>();
                    winner.add(candidate.getName());
                    return winner;
                }
            }

            // Find the candidates with the fewest votes
            int minVotes = Integer.MAX_VALUE;
            List<Candidate> toEliminate = new ArrayList<>();
            for (Candidate candidate : candidates) {
                if (!candidate.isEliminated()) {
                    int votes = candidate.getVotes();
                    if (votes < minVotes) {
                        minVotes = votes;
                        toEliminate.clear();
                        toEliminate.add(candidate);
                    } else if (votes == minVotes) {
                        toEliminate.add(candidate);
                    }
                }
            }

            // If all remaining candidates have the same votes, it's a tie
            if (toEliminate.size() == getRemainingCandidates()) {
                List<String> tiedCandidates = new ArrayList<>();
                for (Candidate candidate : candidates) {
                    if (!candidate.isEliminated()) {
                        tiedCandidates.add(candidate.getName());
                    }
                }
                return tiedCandidates;
            }

            // Eliminate the candidates with the fewest votes and redistribute ballots
            for (Candidate candidate : toEliminate) {
                List<Ballot> redistributedBallots = candidate.eliminate();
                for (Ballot ballot : redistributedBallots) {
                    assignBallotToCandidate(ballot);
                }
            }
        }
    }
}