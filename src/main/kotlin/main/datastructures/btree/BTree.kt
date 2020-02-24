package main.datastructures.btree

interface BTree<K: Number,V> {
  fun put(key: K, value: V)
  fun scan(from: K, to: K): Collection<V>
  fun remove(key: K, value: V)
}