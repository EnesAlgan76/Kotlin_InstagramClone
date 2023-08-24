package com.example.kotlininstagramapp.utils

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.*
import java.io.File

class FileOperations {
  companion object{
      var imagedirList = ArrayList<String>()

      suspend fun listImageFiles(path: String): ArrayList<String>? {
          imagedirList.clear()
         return withContext(Dispatchers.Default){
              val externalStorageDirectory = Environment.getExternalStorageDirectory()
              val imagesDirectory = File(externalStorageDirectory, path)
              var list = listImageFilesRecursive(imagesDirectory)
             return@withContext list
          }

      }

      fun listImageFilesRecursive(directory: File): ArrayList<String>? {
          Log.e("Hata","ÇALIŞTI ${directory}")
          if (!directory.isDirectory) {
              println("Error: The provided path is not a directory.")
              return null
          }

          val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "mp4", "jpeg") // Add more image extensions if needed
          directory.listFiles()?.forEach { file ->
              if (file.isFile &&  imageExtensions.contains(file.extension.lowercase())  ) {
                  imagedirList.add(file.absolutePath)

                  println("---->  ${directory.absolutePath}: ${file.name}")
              } else if (file.isDirectory && directory.name !="WhatsApp Images" ) {
                  println("-----------------------------")
                  listImageFilesRecursive(file)
              }else{
                  println("********************************")
              }
          }

          return imagedirList
      }
  }
}