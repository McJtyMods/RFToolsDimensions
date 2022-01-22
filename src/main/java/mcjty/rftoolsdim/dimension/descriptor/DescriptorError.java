package mcjty.rftoolsdim.dimension.descriptor;

public class DescriptorError {

    public static DescriptorError ERROR(Code code) {
        return new DescriptorError(code);
    }

    public static DescriptorError ERROR(Code code, String data) {
        return new DescriptorError(code, data);
    }

    public static final DescriptorError OK = new DescriptorError(Code.OK);

    private final Code code;
    private final String data;

    public DescriptorError(Code code, String data) {
        this.code = code;
        this.data = data;
    }

    public DescriptorError(Code code) {
        this.code = code;
        this.data = null;
    }

    public boolean isOk() {
        return this == OK;
    }

    public Code getCode() {
        return code;
    }

    public String getData() {
        return data;
    }

    public String getMessage() {
        if (data == null) {
            return code.getMessage();
        } else {
            return code.getMessage() + " " + data;
        }
    }

    public enum Code {
        OK(null),
        ONLY_ONE_BIOME_CONTROLLER("You can only have one biome controller!"),
        ONLY_ONE_TERRAIN("You can only have one terrain type!"),
        ONLY_ONE_TIME("You can only have one time dimlet!"),
        ONLY_ONE_BLOCK("Terrain dimlet only supports one block!"),
        BAD_BLOCK("Bad block!"),
        BAD_FLUID("Bad fluid!"),
        ONLY_ONE_FLUID("Terrain supports only one fluid!"),
        FLUID_HAS_NO_BLOCK("Fluid has no block!"),
        BAD_FEATURE("Bad feature!"),
        BAD_TIME("Bad time!"),
        BAD_ATTRIBUTE("Bad attribute!"),
        BAD_ADMIN_TYPE("Bad admin type!"),
        BAD_TERRAIN_TYPE("Bad terrain type!"),
        BAD_BIOME_CONTROLLER("Bad biome controller!"),
        DANGLING_BLOCKS("Dangling blocks! Blocks should come before either a terrain or a feature!"),
        DANGLING_FLUIDS("Dangling fluids! Fluids should come before either a terrain or a feature!"),
        DANGLING_ATTRIBUTES("Dangling attributes! Attributes should come before a terrain!");

        private final String message;

        Code(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
