package de.growanyway.growanyway.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GrowAnywayConfig {
    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        SERVER = new Server(builder);
        SERVER_SPEC = builder.build();
    }

    private GrowAnywayConfig() {
    }

    public static final class Server {
        public final ModConfigSpec.BooleanValue forceBonemealGrowth;
        public final ModConfigSpec.IntValue bonemealPasses;
        public final ModConfigSpec.BooleanValue accelerateNaturalGrowth;
        public final ModConfigSpec.BooleanValue bonusPlantDrops;
        public final ModConfigSpec.IntValue bonusDropMultiplier;
        public final ModConfigSpec.BooleanValue relaxFeatureSpaceChecks;
        public final ModConfigSpec.IntValue featureClearRadius;
        public final ModConfigSpec.IntValue featureClearHeight;

        private Server(ModConfigSpec.Builder builder) {
            builder.push("growth");
            forceBonemealGrowth = builder
                    .comment("Erzwingt bei Knochenmehl moeglichst sofortiges Wachstum, auch fuer viele modded Pflanzen mit age/stage-Properties.")
                    .define("forceBonemealGrowth", true);
            bonemealPasses = builder
                    .comment("Wie oft Grow Anyway in einem Knochenmehl-Klick Wachstumsschritte anstoesst.")
                    .defineInRange("bonemealPasses", 4, 1, 16);
            accelerateNaturalGrowth = builder
                    .comment("Laesst CropGrowEvent-basierte Pflanzen bei Random Ticks immer wachsen.")
                    .define("accelerateNaturalGrowth", true);
            builder.pop();

            builder.push("drops");
            bonusPlantDrops = builder
                    .comment("Verdoppelt oder vervielfacht Beute von Pflanzen, Blaettern und aehnlichen Wachstumsbloecken.")
                    .define("bonusPlantDrops", true);
            bonusDropMultiplier = builder
                    .comment("Multiplikator fuer passende Pflanzen-Drops.")
                    .defineInRange("bonusDropMultiplier", 2, 1, 16);
            builder.pop();

            builder.push("compatibility");
            relaxFeatureSpaceChecks = builder
                    .comment("Maskiert modded Nachbarbloecke bei Baum-, Pilz- und Azalea-Features fuer die Platzpruefung, ohne die echten Bloecke zu entfernen.")
                    .define("relaxFeatureSpaceChecks", true);
            featureClearRadius = builder
                    .comment("Horizontaler Radius um den Feature-Ursprung, in dem modded Nachbarbloecke fuer die Platzpruefung ignoriert werden duerfen.")
                    .defineInRange("featureClearRadius", 2, 0, 8);
            featureClearHeight = builder
                    .comment("Hoehe ueber dem Ausgangsblock, in der modded Nachbarbloecke fuer die Platzpruefung ignoriert werden duerfen.")
                    .defineInRange("featureClearHeight", 16, 1, 48);
            builder.pop();
        }
    }
}