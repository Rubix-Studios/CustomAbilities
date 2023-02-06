package nl.rubixstudios.partneritems.util.particles.particletrailbuilder;

public enum ParticleColor {

LIMEGREEN(50, 205, 50),
GRAY(128, 128, 128),
RED(255, 0, 0);

    private int red;
    private int green;
    private int blue;

    ParticleColor(int red, int green, int blue) {
        this.red  = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
}





