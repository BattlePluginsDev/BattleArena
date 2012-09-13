package mc.alk.arena.objects.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import mc.alk.arena.objects.MatchState;


@Retention(RetentionPolicy.RUNTIME)
public @interface TransitionEventHandler {
	MatchState begin() default MatchState.NONE;
	MatchState end() default MatchState.NONE;
	EventPriority priority() default EventPriority.NORMAL;
	boolean needsPlayer() default true;
	boolean suppressCastWarnings() default false;
}