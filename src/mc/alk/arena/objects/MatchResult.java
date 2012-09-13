package mc.alk.arena.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mc.alk.arena.objects.teams.Team;


public class MatchResult{
	Team victor =null;
	Set<Team> losers = new HashSet<Team>();

	public MatchResult() {
	}

	public boolean matchComplete(){
		return victor != null;
	}
	
	public void setVictor(Team vic) {
		this.victor= vic;
	}

	public void setLosers(Collection<Team> losers) {
		this.losers = new HashSet<Team>(losers);
	}
	public void addLoser(Team loser) {
		losers.add(loser);
	}

	public Team getVictor() {
		return victor;
	}

	public Set<Team> getLosers() {
		return losers;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("victor=" + victor + " losers=" + losers);
		return sb.toString();
	}

	public String toPrettyString() {
		if (victor == null){
			return "&eThere are no victors yet";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(victor.getTeamSummary() +" &ewins vs ");
		for (Team t: losers){
			sb.append(t.getTeamSummary()+" ");
		}
		
		return sb.toString();
	}

}
