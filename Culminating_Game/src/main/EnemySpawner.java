package main;

import absFrame.Character;
import absFrame.Monster;
import sprite.SlimeKingBoss;
import sprite.WolfMonster;
import sprite.Zombie;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

public class EnemySpawner {
    private static final double MONSTER_SPAWN_INTERVAL_SECONDS = 2.0;
    private static final int MAX_ALIVE_MONSTERS = 35;

    private WorldMap worldMap;
    private Character archer;
    private HashMap<String, Integer> monsterStats;
    private Class<?> resourceOwner;
    private int survivalTimeSeconds;

    private double lastMonsterSpawnTime = 0.0;
    private Random spawnRandom = new Random();

    private BufferedImage zombieWalkSheet;
    private BufferedImage wolfWalkSheet;
    private BufferedImage wolfDashEffect;
    private BufferedImage slimeKingSheet;
    private BufferedImage slimeProjectileImage;

    private SlimeKingBoss slimeKingBoss;
    private boolean bossPhaseStarted = false;

    public EnemySpawner(WorldMap worldMap, Character archer,
                        HashMap<String, Integer> monsterStats,
                        Class<?> resourceOwner,
                        int survivalTimeSeconds) {
        this.worldMap = worldMap;
        this.archer = archer;
        this.monsterStats = monsterStats;
        this.resourceOwner = resourceOwner;
        this.survivalTimeSeconds = survivalTimeSeconds;

        loadEnemyImages();
    }

    private void loadEnemyImages() {
        zombieWalkSheet = ImageLoader.loadImage(resourceOwner, "Zombie.png");
        wolfWalkSheet = ImageLoader.loadImage(resourceOwner, "Wolf.png");
        wolfDashEffect = ImageLoader.loadImage(resourceOwner, "WolfDash.png");
        slimeKingSheet = ImageLoader.loadImage(resourceOwner, "SlimeKing.png");
        slimeProjectileImage = ImageLoader.loadOptionalImage(resourceOwner, "Minislime.png");
    }

    public void setupWorldMonsters() {
        worldMap.clearMonsters();

        for (String roomName : worldMap.getCombatRoomNames()) {
            spawnMonsterInRoom(roomName);
        }
    }

    public void update(double fullTime) {
        if (monsterStats == null || bossPhaseStarted) {
            return;
        }

        if (fullTime >= survivalTimeSeconds) {
            startBossPhase();
            return;
        }

        if (worldMap.getMonsters().size() >= MAX_ALIVE_MONSTERS) {
            return;
        }

        if (fullTime - lastMonsterSpawnTime < MONSTER_SPAWN_INTERVAL_SECONDS) {
            return;
        }

        lastMonsterSpawnTime = fullTime;

        int spawnCount = fullTime >= 50 ? 2 : 1;
        for (int i = 0; i < spawnCount && worldMap.getMonsters().size() < MAX_ALIVE_MONSTERS; i++) {
            spawnMonsterAtRandomZone();
        }
    }

    private void spawnMonsterAtRandomZone() {
        String[] spawnZones = worldMap.getCombatRoomNames();
        if (spawnZones == null || spawnZones.length == 0) {
            return;
        }

        String roomName = spawnZones[spawnRandom.nextInt(spawnZones.length)];
        spawnMonsterInRoom(roomName);
    }

    private void spawnMonsterInRoom(String roomName) {
        Monster monster;

        if (spawnRandom.nextBoolean()) {
            Zombie zombie = new Zombie(monsterStats, 0, 100, 100, 0, 0, 0.25);
            zombie.setWalkAnimation(zombieWalkSheet, 4);
            monster = zombie;
        } else {
            WolfMonster wolf = new WolfMonster(monsterStats, 0, 100, 100, 0, 0, 0.25);
            wolf.setWalkAnimation(wolfWalkSheet, 4);
            wolf.setDashEffectImage(wolfDashEffect);
            monster = wolf;
        }

        placeMonsterRandomlyInRoom(roomName, monster);
        worldMap.addMonster(monster);
    }

    private void placeMonsterRandomlyInRoom(String roomName, Monster monster) {
        int maxAttempts = 20;
        int safeDistanceFromPlayer = 180;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Point spawnPoint = worldMap.getRandomSpawnPointInArea(
                    roomName,
                    spawnRandom,
                    monster.width,
                    monster.height
            );

            monster.setWorldPosition(spawnPoint.x, spawnPoint.y);

            if (!isTooCloseToPlayer(monster, safeDistanceFromPlayer)) {
                return;
            }
        }
    }

    private boolean isTooCloseToPlayer(Monster monster, int safeDistance) {
        int playerCenterX = archer.x + archer.width / 2;
        int playerCenterY = archer.y + archer.height / 2;
        int monsterCenterX = monster.x + monster.width / 2;
        int monsterCenterY = monster.y + monster.height / 2;

        int dx = playerCenterX - monsterCenterX;
        int dy = playerCenterY - monsterCenterY;
        return dx * dx + dy * dy < safeDistance * safeDistance;
    }

    private void startBossPhase() {
        bossPhaseStarted = true;
        worldMap.clearMonsters();

        HashMap<String, Integer> bossStats = new HashMap<String, Integer>();
        bossStats.put("health", 40000);
        bossStats.put("damage", 16);
        bossStats.put("visionRange", 1);
        bossStats.put("speedX", 1);
        bossStats.put("speedY", 1);

        slimeKingBoss = new SlimeKingBoss(bossStats, 0, 260, 260, 0, 0, 1.0);
        slimeKingBoss.setBossAnimation(slimeKingSheet, 4, 280);
        slimeKingBoss.setProjectileImage(slimeProjectileImage);

        worldMap.addMonsterToArea("arena", slimeKingBoss, 0.50, 0.50);
    }

    public boolean isBossPhaseStarted() {
        return bossPhaseStarted;
    }

    public boolean isBossDefeated() {
        return bossPhaseStarted
                && slimeKingBoss != null
                && slimeKingBoss.getHealth() <= 0;
    }

    public SlimeKingBoss getSlimeKingBoss() {
        return slimeKingBoss;
    }
}
