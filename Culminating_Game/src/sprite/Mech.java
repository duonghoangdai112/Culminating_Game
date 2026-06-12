package sprite;

import absFrame.Character;

public class Mech extends Character {

    public Mech(int health, int shield, int mana,
                int speed, int visionRange,
                int cooldown, String name) {

        super(health, shield, mana, speed,
              visionRange, cooldown, name);
    }

    @Override
    public void Ability() {

    }
}