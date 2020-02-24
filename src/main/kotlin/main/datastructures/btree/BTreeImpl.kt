package main.datastructures.btree

import data.Signature
import java.util.*

class BTreeImpl<T : Number> : BTree<T, Signature> {

    private val map = TreeMap<T, MutableSet<Signature>>()

    override fun put(key: T, value: Signature) {
        map.computeIfAbsent(key) { mutableSetOf() }.add(value)
    }

    override fun scan(from: T, to: T): Collection<Signature> {
        return map.subMap(from, to).values.flatten()
    }

    override fun remove(key: T, value: Signature) {
        map.getOrDefault(key, mutableSetOf()).remove(value)
    }
}