package io.github.olgaruban.util

import java.io.Closeable

/**
  * @author Olga Ruban
  * @version 5/23/16, 00:06
  */
trait CloseableResource {

  def using[T <: Closeable, B](resource: T)(block: T => B) = {
    try {
      block(resource)
    } finally {
      if (resource != null) resource.close()
    }
  }

}
