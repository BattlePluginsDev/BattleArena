package mc.alk.arena.controllers.messaging;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.messaging.Channel;
import mc.alk.arena.objects.messaging.Message;
import mc.alk.arena.objects.messaging.MessageOptions.MessageOption;
import mc.alk.arena.objects.teams.Team;
import mc.alk.arena.serializers.MessageSerializer;
import mc.alk.arena.util.TimeUtil;


/**
 * 
 * @author alkarin
 *
 */
public class MatchMessageImpl extends MessageSerializer implements MatchMessageHandler {
	final MatchParams mp;
	final Match match;
	final String typeName;

	public MatchMessageImpl(Match m){
		super(m.getParams().getName());
		this.mp = m.getParams();
		this.match = m;
		typeName = mp.getName();
	}

	@Override
	public void sendOnPreStartMsg(Channel serverChannel,List<Team> teams) {
		sendMessageToTeams(serverChannel,teams,"prestart","server_prestart", mp.getSecondsTillMatch());
	}

	@Override
	public void sendOnStartMsg(Channel serverChannel, List<Team> teams) {
		sendMessageToTeams(serverChannel,teams,"start","server_start",null);
	}

	private void sendMessageToTeams(Channel serverChannel, List<Team> teams, String path, String serverpath, Integer seconds){
		final String nTeamPath = getStringPathFromSize(teams.size()); 
		Message message = getMessage("match."+ nTeamPath+"."+path);
		Message serverMessage = getMessage("match."+ nTeamPath+"."+serverpath);
		Set<MessageOption> ops = message.getOptions();
		if (serverChannel != Channel.NullChannel){
			ops.addAll(serverMessage.getOptions());	
		}

		String msg = message.getMessage();
		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), ops.size(), teams.size(), message, ops);

		msgf.formatCommonOptions(teams,seconds);
		for (Team t: teams){
			msgf.formatTeamOptions(t,false);
			msgf.formatTwoTeamsOptions(t, teams);
			msgf.formatTeams(teams);
			String newmsg = msgf.getFormattedMessage(message);
			t.sendMessage(newmsg);
		}

		if (serverChannel != Channel.NullChannel){
			msg = msgf.getFormattedMessage(serverMessage);
			serverChannel.broadcast(msg);
		}
	}
	
	@Override
	public void sendOnVictoryMsg(Channel serverChannel, Team victor, Collection<Team> losers) {
		final String nTeamPath = getStringPathFromSize(losers.size()+1); 
		sendVictory(serverChannel,victor,losers,mp,"match."+nTeamPath+".victory","match."+nTeamPath+".loss",
				"match."+nTeamPath+".server_victory");
	}

	public void sendYourTeamNotReadyMsg(Team t1) {
		Message message = getMessage("match"+typeName+".your_team_not_ready");
		Set<MessageOption> ops = message.getOptions();

		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), ops.size(), 1, message, ops);
		msgf.formatTeamOptions(t1, false);
		t1.sendMessage(msgf.getFormattedMessage(message));
	}

	public void sendOtherTeamNotReadyMsg(Team t1) {
		Message message = getMessage("match."+typeName+".other_team_not_ready");
		Set<MessageOption> ops = message.getOptions();

		MessageFormatter msgf = new MessageFormatter(this, match.getParams(), ops.size(), 1, message, ops);
		msgf.formatTeamOptions(t1, false);
		t1.sendMessage(msgf.getFormattedMessage(message));
	}

	public void sendOnIntervalMsg(Channel serverChannel, Team currentLeader, int remaining) {
		TimeUtil.testClock();
		final String timeStr = TimeUtil.convertSecondsToString(remaining);
		String msg;
		if (currentLeader == null){
			msg = match.getParams().getPrefix()+"&e ends in &4" +timeStr;			
		} else {
			msg = match.getParams().getPrefix()+"&e ends in &4" +timeStr +".&6"+
					currentLeader.getDisplayName()+"&e leads with &2" + currentLeader.getNKills() +
					"&e kills &4"+currentLeader.getNDeaths()+"&e deaths";				
		}
		match.sendMessage(msg);		
	}
	public void sendTimeExpired(Channel serverChannel) {}

	@Override
	public void sendOnDrawMsg(Channel serverChannel, Collection<Team> participants) {
		final String nTeamPath = getStringPathFromSize(participants.size()); 
		sendVictory(serverChannel,null,participants,mp,"match."+nTeamPath+".draw","match."+nTeamPath+".draw",
				"match."+nTeamPath+".server_draw");
	}
	

}
