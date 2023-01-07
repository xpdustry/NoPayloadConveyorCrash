/*
 * This file is part of NoPayloadConveyorCrashPlugin. No more payload conveyor crashes.
 *
 * MIT License
 *
 * Copyright (c) 2023 Xpdustry
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fr.xpdustry.patches;

import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.PayloadConveyor;

public class FixedPayloadConveyor extends PayloadConveyor {

    public FixedPayloadConveyor(String name) {
        super(name);
    }

    public class FixedPayloadBuild extends PayloadConveyor.PayloadConveyorBuild {

        @Override
        public void updateTile() {
            if (!enabled) {
                return;
            }

            if (item != null) {
                item.update(null, this);
            }

            lastInterp = curInterp;
            curInterp = fract();
            // rollover skip
            if (lastInterp > curInterp) {
                lastInterp = 0f;
            }
            progress = time() % moveTime;

            updatePayload();
            if (item != null && next == null) {
                PayloadBlock.pushOutput(item, progress / moveTime);
            }

            int curStep = curStep();
            if (curStep > step) {
                boolean valid = step != -1;
                step = curStep;
                boolean had = item != null;

                if (valid && stepAccepted != curStep && item != null) {
                    if (next != null) {
                        // trigger update forward
                        next.updateTile();

                        // PATCH HERE
                        if (next != null && next.acceptPayload(this, item)) {
                            // move forward.
                            next.handlePayload(this, item);
                            item = null;
                            moved();
                        }
                    } else if (!blocked) {
                        // dump item forward
                        if (item.dump()) {
                            item = null;
                            moved();
                        }
                    }
                }

                if (had && item != null) {
                    moveFailed();
                }
            }
        }
    }
}
