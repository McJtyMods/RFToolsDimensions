package mcjty.rftoolsdim.compat;

import mcjty.lostcities.api.ILostCities;

import java.util.function.Function;

public class LostCityInternal {

    static ILostCities lostCities = null;

    public static class GetLostCity implements Function<ILostCities, Void> {

        @Override
        public Void apply(ILostCities tm) {
            lostCities = tm;
            return null;
        }
    }
}
