import processing.core.PApplet;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    PApplet p;

    ArrayList<SoundFile> tearsFireSounds = new ArrayList<>();
    ArrayList<SoundFile> tearsImpactSounds = new ArrayList<>();
    ArrayList<SoundFile> powerupSounds = new ArrayList<>();

    ArrayList<SoundFile> treasureRoomEnterSounds = new ArrayList<>();

    ArrayList<SoundFile> plopSounds = new ArrayList<>();

    ArrayList<SoundFile> bossDefeatSounds = new ArrayList<>();

    ArrayList<SoundFile> bossThemes = new ArrayList<>();

    ArrayList<SoundFile> basementTheme = new ArrayList<>();

    ArrayList<SoundFile> bossBeatenTheme = new ArrayList<>();

    ArrayList<SoundFile> whistleSounds = new ArrayList<>();

    ArrayList<SoundFile> fartSounds = new ArrayList<>();


    Map<String, ArrayList<SoundFile>> sounds = new HashMap<>();



    SoundManager(PApplet p) {
        this.p = p;
        loadSounds();

        Signals.PlaySound.connect(this::playRandomSound);
        Signals.PlayMusic.connect(this::playMusic);
        Signals.StopMusic.connect(data -> {
            stopMusic();
        });
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

        powerupSounds.add(new SoundFile(p, "data/sfx/Powerup1.mp3"));

        sounds.put("powerup", powerupSounds);

        treasureRoomEnterSounds.add(new SoundFile(p, "data/sfx/Treasure_Room_Enter.mp3"));

        sounds.put("treasure_room_enter", treasureRoomEnterSounds);

        plopSounds.add(new SoundFile(p, "data/sfx/plop.mp3"));

        sounds.put("plop", plopSounds);

        bossDefeatSounds.add(new SoundFile(p, "data/sfx/boss_defeat.mp3"));

        sounds.put("boss_defeat", bossDefeatSounds);

        bossThemes.add(new SoundFile(p, "data/music/boss_theme.mp3"));
        bossThemes.getFirst().amp(0.4f);

        sounds.put("boss_theme", bossThemes);

        basementTheme.add(new SoundFile(p, "data/music/basement_theme.mp3"));
        basementTheme.getFirst().amp(0.5f);

        sounds.put("basement_theme", basementTheme);

        bossBeatenTheme.add(new SoundFile(p, "data/music/boss_beaten.mp3"));
        bossBeatenTheme.getFirst().amp(0.5f);

        sounds.put("boss_beaten_theme", bossBeatenTheme);

        whistleSounds.add(new SoundFile(p, "data/sfx/whistle.mp3"));

        sounds.put("whistle", whistleSounds);

        fartSounds.add(new SoundFile(p, "data/sfx/fart.mp3"));

        sounds.put("fart", fartSounds);
    }

    public void playRandomSound(String sound) {

        ArrayList<SoundFile> soundFiles = sounds.get(sound);
        int randomIndx = (int) p.random(soundFiles.size());
        SoundFile soundFile = soundFiles.get(randomIndx);
        soundFile.play();
    }

    public void playMusic(String sound) {
        if (sound.equals("boss_theme")) {
            if (basementTheme.getFirst().isPlaying()) {
                basementTheme.getFirst().stop();
            }
            if (bossBeatenTheme.getFirst().isPlaying()) {
                bossBeatenTheme.getFirst().stop();
            }
        }
        if (sound.equals("basement_theme")) {
            if (bossThemes.getFirst().isPlaying()) {
                bossThemes.getFirst().stop();
            }
            if (bossBeatenTheme.getFirst().isPlaying()) {
                bossBeatenTheme.getFirst().stop();
            }
        }
        if (sound.equals("boss_beaten_theme")) {
            if (basementTheme.getFirst().isPlaying()) {
                basementTheme.getFirst().stop();
            }
            if (bossThemes.getFirst().isPlaying()) {
                bossThemes.getFirst().stop();
            }
        }
        ArrayList<SoundFile> soundFiles = sounds.get(sound);
        int randomIndx = (int) p.random(soundFiles.size());
        SoundFile soundFile = soundFiles.get(randomIndx);
        soundFile.loop();
    }

    public void stopMusic() {
        if (basementTheme.getFirst().isPlaying()) {
            basementTheme.getFirst().stop();
        }
        if (bossThemes.getFirst().isPlaying()) {
            bossThemes.getFirst().stop();
        }
        if (bossBeatenTheme.getFirst().isPlaying()) {
            bossBeatenTheme.getFirst().stop();
        }
    }
}
