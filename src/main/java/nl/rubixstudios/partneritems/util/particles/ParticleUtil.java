package nl.rubixstudios.partneritems.util.particles;

import nl.rubixstudios.partneritems.util.particles.particletrailbuilder.ParticleTrailBuilder;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Random;

public class ParticleUtil {

    private static Random rand = new Random();

    public static void runColoredParticlesAtMultipleLocations(final Location[] locations, int amount, float red, float green, float blue, Effect effect){
        if (locations.length == 0) {
            System.out.println("The location length is zero, returning.");
            return;
        }

        for (final Location loc : locations){
            if (loc == null){
                System.out.println("LOCATION IS NULL - NOT GOOD");
                continue;
            }
            runColoredParticles(loc, amount, red, green, blue, effect);
        }
    }

    public static void runColoredParticlesCubeAtPlayer(Player player, int amount, float red, float green, float blue, Effect effect){

        runColoredParticlesAtMultipleLocations(drawSmallCubeAtPlayerLocation(player), amount, red, green, blue, effect);
    }

    public static void runColoredParticles(Location loc, int amount, float red, float green, float blue, Effect effect){
        if (loc == null){
            System.out.println("Location is null - returning");
            return;
        }

        for (int i = 0; i < amount; i++) {
            loc.getWorld().spigot().playEffect(loc, effect, 0, 1, red / 255, green / 255, blue / 255, 1, 0, 16);
        }
    }

    public static void createParticleTrailForArrow(final Arrow arrow, final int red, final int green, final int blue, Effect effect){
        final ParticleTrailBuilder particleBuilder = new ParticleTrailBuilder(arrow);
        particleBuilder.setRed(red).setGreen(green).setBlue(blue).setEffect(effect).build();
    }


    public static void runColoredParticleTrailByMetadata(final String metadata, final Arrow[] arrows, final Effect effect){
        for (final Arrow arrow : arrows) {
            final ParticleTrailBuilder particleBuilder = new ParticleTrailBuilder(arrow);
            particleBuilder.withMetadata(metadata).setEffect(effect).build();
        }
    }

    // Draws small cube at player location
    private static Location[] drawSmallCubeAtPlayerLocation(Player player){

        final Location loc = player.getLocation();

        final Location loc2 = player.getLocation();
        final Location loc3 = player.getLocation();
        final Location loc4 = player.getLocation();
        final Location loc5 = player.getLocation();
        final Location loc6 = player.getLocation();

        loc.setX(loc.getX() + -getRandomValueX());
        loc.setY(loc.getY() + 1);
        loc.setZ(loc.getZ() + getRandomValueZ());

        loc2.setX(loc2.getX() + getRandomValueX());
        loc2.setY(loc2.getY() + 1);
        loc2.setZ(loc2.getZ() + -getRandomValueZ());

        loc3.setX(loc3.getX() + -getRandomValueX());
        loc3.setY(loc3.getY() + 1.5);
        loc3.setZ(loc3.getZ() + getRandomValueZ());

        loc4.setX(loc4.getX() + getRandomValueX());
        loc4.setY(loc4.getY() + 1.5);
        loc4.setZ(loc4.getZ() + -getRandomValueZ());

        loc5.setX(loc5.getX() + -getRandomValueX());
        loc5.setY(loc5.getY() + 1);
        loc5.setZ(loc5.getZ() + -getRandomValueZ());

        loc6.setX(loc6.getX() + getRandomValueX());
        loc6.setY(loc6.getY() + 1.5);
        loc6.setZ(loc6.getZ() + getRandomValueZ());

        return new Location[]{
                loc,
                loc2,
                loc3,
                loc4,
                loc5,
                loc6
        };
    }

    private static double getRandomValueX(){
        return 0.3 + (0.4 - 0.3) * rand.nextDouble();
    }

    private static double getRandomValueY(){
        return -0.3 + (0.3 - -0.3) * rand.nextDouble();
    }

    private static double getRandomValueZ(){
        return 0.3 + (0.4 - 0.3) * rand.nextDouble();
    }
}
