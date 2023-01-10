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

import arc.Events;
import arc.math.geom.Point2;
import arc.struct.IntSet;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Strings;
import java.util.function.Consumer;
import java.util.function.Function;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.ctype.MappableContent;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.mod.Plugin;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.logic.LogicBlock.LogicBuild;
import mindustry.world.blocks.payloads.PayloadConveyor.PayloadConveyorBuild;

@SuppressWarnings("unused")
public final class NoPayloadConveyorCrashPlugin extends Plugin {

    private final IntSet processorIndex = new IntSet();
    private final ObjectMap<String, MappableContent>[] contentNameMap;

    @SuppressWarnings("unchecked")
    public NoPayloadConveyorCrashPlugin() {
        try {
            final var field = Vars.content.getClass().getDeclaredField("contentNameMap");
            field.setAccessible(true);
            this.contentNameMap = (ObjectMap<String, MappableContent>[]) field.get(Vars.content);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        this.replaceBlock("payload-conveyor", FixedPayloadConveyor::new, block -> {
            block.requirements(Category.units, ItemStack.with(Items.graphite, 10, Items.copper, 10));
            block.canOverdrive = false;
        });

        this.replaceBlock("payload-router", FixedPayloadRouter::new, block -> {
            block.requirements(Category.units, ItemStack.with(Items.graphite, 15, Items.copper, 10));
            block.canOverdrive = false;
        });

        this.replaceBlock("reinforced-payload-conveyor", FixedPayloadConveyor::new, block -> {
            block.requirements(Category.units, ItemStack.with(Items.tungsten, 10));
            block.moveTime = 35f;
            block.canOverdrive = false;
            block.health = 800;
            block.researchCostMultiplier = 4f;
            block.underBullets = true;
        });

        this.replaceBlock("reinforced-payload-router", FixedPayloadRouter::new, block -> {
            block.requirements(Category.units, ItemStack.with(Items.tungsten, 15));
            block.moveTime = 35f;
            block.health = 800;
            block.canOverdrive = false;
            block.researchCostMultiplier = 4f;
            block.underBullets = true;
        });

        this.setupLogicUnlink();
    }

    private <T extends Block> void replaceBlock(
            final String name, Function<String, T> constructor, final Consumer<T> modifiers) {
        try {
            // Get the corresponding field
            final var field = Blocks.class.getField(Strings.kebabToCamel(name));
            final var block = (Block) field.get(null);
            // Get the index of the block to replace
            final var index = Vars.content.blocks().indexOf(block);
            // Get the id of the block to replace
            final var id = block.id;
            // Remove the old block
            this.contentNameMap[block.getContentType().ordinal()].remove(block.name);
            Vars.content.blocks().remove(block);
            // Creates the replacement, it will register itself in the contentNameMap
            final var replacement = constructor.apply(name);
            // Apply the modifiers of the replaced block
            modifiers.accept(replacement);
            replacement.id = id;
            // Substitute the old block with the new one in the tech tree
            replacement.techNode = block.techNode;
            replacement.techNode.content = replacement;
            // Since the block adds itself to the content list, remove to insert it to the right index
            Vars.content.blocks().remove(replacement);
            Vars.content.blocks().insert(index, replacement);
            // Initialize the block
            replacement.init();
            // Set the field to the new block
            field.set(null, replacement);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupLogicUnlink() {
        Events.on(EventType.WorldLoadEvent.class, event -> {
            processorIndex.clear();
        });

        Events.on(EventType.TilePreChangeEvent.class, event -> {
            processorIndex.remove(event.tile.pos());
        });

        Events.on(EventType.TileChangeEvent.class, event -> {
            if (event.tile.build instanceof LogicBuild building) {
                processorIndex.add(building.pos());
            }
        });

        Events.run(Trigger.update, () -> {
            final var iterator = processorIndex.iterator();
            while (iterator.hasNext) {
                final var position = iterator.next();
                if (!(Vars.world.build(position) instanceof final LogicBuild processor)) {
                    Log.warn(
                            "[NoPayloadConveyorCrashPlugin] Logic processor at @ is not a LogicBuild instance!",
                            Point2.unpack(position));
                    iterator.remove();
                    continue;
                }
                for (final var link : processor.links) {
                    // de-configure the link if it is a payload conveyor
                    if (link.active && Vars.world.build(link.x, link.y) instanceof PayloadConveyorBuild) {
                        processor.configure(Point2.pack(link.x, link.y));
                    }
                }
            }
        });
    }
}
