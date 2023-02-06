package nl.rubixstudios.partneritems.util.particles.particletrailbuilder;

import nl.rubixstudios.partneritems.PartnerItems;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;

public class ParticleTrailBuilder {
    private int red;
    private int green;
    private int blue;

    private final Arrow arrow;
    private final ParticleTrailObject particleTrailObject;

    private Effect effect;

    public ParticleTrailBuilder(Arrow arrow) {
        this.arrow = arrow;
        this.particleTrailObject = new ParticleTrailObject();
    }

    public ParticleTrailBuilder setRed(int value) {
        this.red = value;
        return this;
    }

    public ParticleTrailBuilder setGreen(int value) {
        this.green = value;
        return this;
    }

    public ParticleTrailBuilder setBlue(int value) {
        this.blue = value;
        return this;
    }

    public ParticleTrailBuilder setEffect(Effect effect) {
        this.effect = effect;
        return this;
    }

    // TODO kijk of dit beter kan
    public ParticleTrailBuilder withMetadata(String metadata){
        return this;
    }

    public ParticleTrailBuilder setColor(final ParticleColor particleColor) {
        setRed(particleColor.getRed());
        setGreen(particleColor.getGreen());
        setBlue(particleColor.getBlue());
        return this;
    }

    public ParticleTrailObject build() {
        particleTrailObject.setArrow(arrow);
        particleTrailObject.setRed(red);
        particleTrailObject.setGreen(green);
        particleTrailObject.setBlue(blue);
        particleTrailObject.setEffect(effect);

        PartnerItems.getInstance().getParticleTrailManager().getArrows().add(particleTrailObject);

        return particleTrailObject;
    }
}
