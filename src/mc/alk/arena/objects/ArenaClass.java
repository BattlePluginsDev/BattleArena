package mc.alk.arena.objects;

import java.util.List;

import mc.alk.arena.util.EffectUtil.EffectWithArgs;

import org.bukkit.inventory.ItemStack;

public class ArenaClass {
	public static final Integer DEFAULT = Integer.MAX_VALUE;
	
	final String name;
	final List<ItemStack> items;
	final List<EffectWithArgs> effects;
	final String prettyName;
	public ArenaClass(String name, String prettyName, List<ItemStack> items, List<EffectWithArgs> effects){
		this.name = name;
		this.items = items;
		this.effects = effects;
		this.prettyName = prettyName;
	}
	public String getName() {
		return name;
	}
	public List<ItemStack> getItems() {
		return items;
	}

	public List<EffectWithArgs> getEffects() {
		return effects;
	}

	public String toString(){
		return "[ArenaClass "+name+" items="+items +" enchants=" + effects+"]";
	}
	public String getPrettyName() {
		return prettyName != null ? prettyName : name;
	}
}
