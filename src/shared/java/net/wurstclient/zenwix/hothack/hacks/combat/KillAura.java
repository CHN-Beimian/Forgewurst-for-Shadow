package net.wurstclient.zenwix.hothack.hacks.combat;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wurstclient.forge.compatibility.WEntity;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.loader.ModEnemyLoader;
import net.wurstclient.forge.loader.ModFriendsLoader;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.STimer;
import net.wurstclient.forge.utils.Wrapper;
import net.wurstclient.zenwix.hothack.entry.Hack;

public class KillAura extends Hack{
	public EntityLivingBase target;
	
	public static int CPS=10;
	public static double reach=3.85;
	private final STimer timer = new STimer();
	private final STimer cps = new STimer();
	
	public KillAura() {
		super("KillAura","Killaura");
		setKey(new KeyBinding("Shadow.killaura", Keyboard.KEY_B, "Shadow"));
	}
	@Override
	public void onTicks() {
	}
	@Override
	public void onUpdate() {
		 EntityPlayerSP player = mc.player;
		 World world=mc.world;
			double rangeSq = Math.pow(reach, 2);
			double hypixelTimer = 0.1 * 1000;
			int delayValue = (20 / CPS) * 50;
			
			
			if (player.getCooledAttackStrength(0) < 1)
				return;
			
			Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
					.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
					.filter(e -> !e.isDead && e.getHealth() > 0)
					.filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq).filter(e -> e != player)
					.filter(e -> !(e instanceof EntityFakePlayer));

			stream = stream.filter(e -> !(e instanceof EntityVillager));
			
			target = stream.min(Priority.ANGLE.comparator).orElse(null);
			if(target==null) 
				return;
				
			
				
				if (cps.check(delayValue)) {
					mc.playerController.attackEntity(mc.player, target);
					mc.player.swingArm(EnumHand.MAIN_HAND);
					cps.reset();
						}
				
				}
	
	@Override
	public void onWorldRender() {
		if (target == null)
			return;

		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glPushMatrix();
		GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY,
				-TileEntityRendererDispatcher.staticPlayerZ);

		AxisAlignedBB box = new AxisAlignedBB(BlockPos.ORIGIN);
		float p = (target.getMaxHealth() - target.getHealth()) / target.getMaxHealth();
		float red = p * 2F;
		float green = 2 - red;

		GL11.glTranslated(target.posX, target.posY, target.posZ);
		GL11.glTranslated(0, 0.05, 0);
		GL11.glScaled(target.width, target.height, target.width);
		GL11.glTranslated(-0.5, 0, -0.5);

		if (p < 1) {
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glScaled(p, p, p);
			GL11.glTranslated(-0.5, -0.5, -0.5);
		}

		GL11.glColor4f(red, green, 0, 0.25F);
		GL11.glBegin(GL11.GL_QUADS);
		RenderUtils.drawSolidBox(box);
		GL11.glEnd();

		GL11.glColor4f(red, green, 0, 0.5F);
		GL11.glBegin(GL11.GL_LINES);
		RenderUtils.drawOutlinedBox(box);
		GL11.glEnd();

		GL11.glPopMatrix();

		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	public void MatchTarget() {
		World world=mc.world;
		double rangeSq = Math.pow(reach, 2);
		
		
		Stream<EntityLivingBase> stream = world.loadedEntityList.parallelStream()
				.filter(e -> e instanceof EntityLivingBase).map(e -> (EntityLivingBase) e)
				.filter(e -> !e.isDead && e.getHealth() > 0)
				.filter(e -> WEntity.getDistanceSq(player, e) <= rangeSq).filter(e -> e != player)
				.filter(e -> !(e instanceof EntityFakePlayer));


		target = stream.min(Priority.DISTANCE.comparator).orElse(null);

			if (!(target instanceof EntityPlayer))
				return;
		
	}
	private enum Priority {
		DISTANCE("Distance", e -> WEntity.getDistanceSq(WMinecraft.getPlayer(), e)),

		ANGLE("Angle", e -> RotationUtils.getAngleToLookVec(e.getEntityBoundingBox().getCenter())),

		HEALTH("Health", e -> e.getHealth());

		private final String name;
		private final Comparator<EntityLivingBase> comparator;

		private Priority(String name, ToDoubleFunction<EntityLivingBase> keyExtractor) {
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
