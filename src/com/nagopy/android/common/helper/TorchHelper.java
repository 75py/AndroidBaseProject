/*
 * Copyright (C) 2013 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.common.helper;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

/**
 * フラッシュライトを使用するためのクラス.
 */
public class TorchHelper {

    /** カメラ */
    private Camera mCamera;

    /** ライトの状態を保持するフィールド */
    private Boolean mStatus = false;

    private static final TorchHelper instance = new TorchHelper();

    private TorchHelper() {
    }

    /**
     * インスタンスを取得する.
     */
    public static TorchHelper getInstance() {
        return instance;
    }

    /**
     * ライトを点灯する.
     *
     * @return ライトが点灯すればtrue、失敗した場合はfalseを返す。
     */
    public synchronized Boolean on() {
        if (isON()) {
            return true;
        }

        try {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.startPreview();
            mStatus = true;
            return true;
        } catch (Exception e) {
            mStatus = false;
            return false;
        }
    }

    /**
     * ライトを消灯する.
     */
    public synchronized void off() {
        if (isON()) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ライトが点灯しているかを取得する.
     *
     * @return ライトが点灯している場合はtrue、それ以外はfalse
     */
    public synchronized Boolean isON() {
        return mStatus && mCamera != null;
    }

    /**
     * ライトのオン・オフを切り替える.
     *
     * @return ライトが点灯している場合はtrue、それ以外はfalse
     */
    public synchronized Boolean toggle() {
        if (isON()) {
            off();
        } else {
            on();
        }
        return isON();
    }

}
