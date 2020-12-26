package ru.zakoulov.navigatorx.viewmodel

import org.junit.Test
import ru.zakoulov.navigatorx.data.PathDot

import org.junit.Assert.*

class MapPathResolverTest {
    @Test
    fun `test find path between the same dots`() {
        val mapPathResolver = MapPathResolver()
        val foundedPath = mapPathResolver.findPath(
            pathDots = PATH_DOTS,
            pathConnections = PATH_CONNECTIONS,
            startDot = "1",
            finishDot = "1"
        )
        assertNotNull(foundedPath)
        val floorPaths = foundedPath!!.floorPaths
        assertEquals(1, floorPaths.size)
        assertTrue(0 in floorPaths)
        val pathsOnFloor = foundedPath.floorPaths[0]!!
        assertEquals(1, pathsOnFloor.size)

        val pathInfo = pathsOnFloor[0]
        assertEquals(listOf(PATH_DOTS["1"]), pathInfo.path)
        assertEquals(Path.BreakType.DESTINATION, pathInfo.breakType)
        assertEquals(0f, foundedPath.virtualDist)
    }

    @Test
    fun `test find non existed path`() {
        val mapPathResolver = MapPathResolver()
        val foundedPath = mapPathResolver.findPath(
            pathDots = PATH_DOTS,
            pathConnections = PATH_CONNECTIONS,
            startDot = "1",
            finishDot = "8"
        )
        assertNull(foundedPath)
    }

    @Test
    fun `test find nearest path`() {
        val mapPathResolver = MapPathResolver()
        val foundedPath = mapPathResolver.findPath(
            pathDots = PATH_DOTS,
            pathConnections = PATH_CONNECTIONS,
            startDot = "1",
            finishDot = "4"
        )
        assertNotNull(foundedPath)
        val floorPaths = foundedPath!!.floorPaths
        assertEquals(1, floorPaths.size)
        assertTrue(0 in floorPaths)
        val pathsOnFloor = foundedPath.floorPaths[0]!!
        assertEquals(1, pathsOnFloor.size)

        val pathInfo = pathsOnFloor[0]
        assertEquals(listOf(
            PATH_DOTS["1"],
            PATH_DOTS["4"]
        ), pathInfo.path)
        assertEquals(Path.BreakType.DESTINATION, pathInfo.breakType)
        assertEquals(48f, foundedPath.virtualDist)
    }

    @Test
    fun `test find path between floors`() {
        val mapPathResolver = MapPathResolver()
        val foundedPath = mapPathResolver.findPath(
            pathDots = PATH_DOTS,
            pathConnections = PATH_CONNECTIONS,
            startDot = "1",
            finishDot = "7"
        )
        assertNotNull(foundedPath)
        val floorPaths = foundedPath!!.floorPaths
        assertEquals(2, floorPaths.size)
        assertTrue(0 in floorPaths)
        assertTrue(1 in floorPaths)

        val pathsOnFloor0 = foundedPath.floorPaths[0]!!
        assertEquals(2, pathsOnFloor0.size)

        val pathInfo1 = pathsOnFloor0[0]
        assertEquals(listOf(
            PATH_DOTS["1"],
            PATH_DOTS["4"],
        ), pathInfo1.path)
        assertEquals(Path.BreakType.FLOOR_UP, pathInfo1.breakType)

        val pathInfo2 = pathsOnFloor0[1]
        assertEquals(listOf(
            PATH_DOTS["7"]
        ), pathInfo2.path)
        assertEquals(Path.BreakType.DESTINATION, pathInfo2.breakType)

        val pathsOnFloor2 = foundedPath.floorPaths[1]!!
        assertEquals(1, pathsOnFloor2.size)

        val pathInfo3 = pathsOnFloor2[0]
        assertEquals(listOf(
            PATH_DOTS["5"],
            PATH_DOTS["6"],
        ), pathInfo3.path)
        assertEquals(Path.BreakType.FLOOR_DOWN, pathInfo3.breakType)

        assertEquals(445f, foundedPath.virtualDist)
    }

    companion object {
        private val PATH_DOTS = mapOf(
            "1" to PathDot(
                id = "1",
                positionX = 2f,
                positionY = 3f,
                floor = 0
            ),
            "2" to PathDot(
                id = "2",
                positionX = 2f,
                positionY = 30f,
                floor = 0
            ),
            "3" to PathDot(
                id = "3",
                positionX = 50f,
                positionY = 30f,
                floor = 0
            ),
            "4" to PathDot(
                id = "4",
                positionX = 50f,
                positionY = 3f,
                floor = 0
            ),
            "5" to PathDot(
                id = "5",
                positionX = 50f,
                positionY = 3f,
                floor = 1
            ),
            "6" to PathDot(
                id = "6",
                positionX = 50f,
                positionY = 300f,
                floor = 1
            ),
            "7" to PathDot(
                id = "7",
                positionX = 50f,
                positionY = 300f,
                floor = 0
            ),
            "8" to PathDot(
                id = "8",
                positionX = 30f,
                positionY = 20f,
                floor = 4
            )
        )

        private val PATH_CONNECTIONS = mapOf(
            "1" to listOf("2", "4"),
            "2" to listOf("1", "3"),
            "3" to listOf("2", "4"),
            "4" to listOf("1", "3", "5"),
            "5" to listOf("4", "6"),
            "6" to listOf("5", "7"),
            "7" to listOf("6")
        )
    }
}
