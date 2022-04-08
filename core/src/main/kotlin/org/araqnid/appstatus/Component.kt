package org.araqnid.appstatus

interface Component {
    companion object {
        fun from(id: String, name: String, supplier: () -> Report) = object : Component {
            override val id: String
                get() = id
            override val name: String
                get() = name

            override fun report(): Report {
                return supplier()
            }

            override fun toString(): String {
                return "Component(id=$id, name=$name)"
            }
        }

        fun info(id: String, name: String, supplier: () -> String): Component = object : Component {
            override val id: String
                get() = id
            override val name: String
                get() = name

            override fun report(): Report {
                return Report(null, supplier())
            }

            override fun toString(): String {
                return "Component(id=$id, name=$name)"
            }
        }

        fun from(id: String, name: String, report: Report): Component = FixedComponent(id, name, report)
        fun info(id: String, name: String, text: String): Component = FixedComponent(id, name, Report(null, text))

    }

    val id: String
    val name: String
    fun report(): Report
}

fun Component.mapReport(mapper: (Report) -> Report): Component =
    Component.from(id, name) { mapper(report()) }

fun Component.mapText(mapper: (String) -> String): Component =
    mapReport { (status, text) -> Report(status, mapper(text)) }

private data class FixedComponent(override val id: String, override val name: String, val report: Report) : Component {
    override fun report(): Report {
        return report
    }
}

fun Component.limitPriority(maxStatus: Report.Status): Component {
    return object : Component {
        override val id: String
            get() = this@limitPriority.id
        override val name: String
            get() = this@limitPriority.name

        override fun report(): Report {
            return this@limitPriority.report().limitPriority(maxStatus)
        }
    }
}