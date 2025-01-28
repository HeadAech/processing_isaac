import processing.core.PApplet;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    PApplet p;

    ArrayList<SoundFile> tearsFireSounds = new ArrayList<>();
    ArrayList<SoundFile> tearsImpactSounds = new ArrayList<>();

    Map<String, ArrayList<SoundFile>> sounds = new HashMap<>();

    SoundManager(PApplet p) {
        this.p = p;
        loadSounds();

        Signals.PlaySound.connect(this::playRandomSound);
    }

    private void loadSounds() {

        tearsFireSounds.add(new SoundFile(p, "data/sfx/Tears_Fire_0.mp3"));
        tearsFireSounds.add(new SoundFile(p, "data/sfx/Tears_Fire_1.mp3"));
        tearsFireSounds.add(new SoundFile(p, "data/sfx/Tears_Fire_2.mp3"));

        sounds.put("tear_fire", tearsFireSounds);

        tearsImpactSounds.add(new SoundFile(p, "data/sfx/TearImpacts0.mp3"));
        tearsImpactSounds.add(new SoundFile(p, "data/sfx/TearImpacts1.mp3"));
        tearsImpactSounds.add(new SoundFile(p, "data/sfx/TearImpacts2.mp3"));

        sounds.put("tear_impact", tearsImpactSounds);
    }

    public void playRandomSound(String sound) {
        ArrayList<SoundFile> soundFiles = sounds.get(sound);
        int randomIndx = (int) p.random(soundFiles.size());
        SoundFile soundFile = soundFiles.get(randomIndx);
        soundFile.play();
    }
}
