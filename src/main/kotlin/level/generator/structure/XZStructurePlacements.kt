package level.generator.structure

class XZStructurePlacements(private val placements: List<Placement>) {

    fun evaluate(x: Int, z: Int): Structure? {
        for (placement in placements) {
            val minX = x - x.mod(placement.structure.x)
            val minZ = z - z.mod(placement.structure.z)
            if (placement.placementEvaluator.invoke(minX, minZ)) {
                return placement.structure
            }
        }
        return null
    }

}

class Placement(
    val structure: Structure,
    val placementEvaluator: (minX: Int, minZ: Int) -> Boolean
)