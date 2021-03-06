/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun io(f: suspend () -> Unit) {
    withContext(Dispatchers.IO) { f.invoke() }
}

fun CoroutineScope.launchInIO(f: suspend CoroutineScope.() -> Unit) {
    launch(Dispatchers.IO) {
        f.invoke(this)
    }
}

suspend fun ui(f: suspend () -> Unit) {
    withContext(Dispatchers.Main) { f.invoke() }
}

fun CoroutineScope.launchInUI(f: suspend CoroutineScope.() -> Unit) {
    launch(Dispatchers.Main) {
        f.invoke(this)
    }
}
