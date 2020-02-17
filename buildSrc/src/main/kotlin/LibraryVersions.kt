import kotlin.reflect.full.memberProperties

object LibraryVersions {
    const val jetty = "9.4.26.v20200117"
    const val jackson = "2.10.2"
    const val guava = "28.2-jre"
    const val resteasy = "3.1.4.Final"
    const val guice = "4.2.1"
    const val slf4j = "1.7.30"
    const val hamkrest = "1.7.0.0"

    fun toMap() =
            LibraryVersions::class.memberProperties
                    .associate { prop -> prop.name to prop.getter.call() as String }
}
