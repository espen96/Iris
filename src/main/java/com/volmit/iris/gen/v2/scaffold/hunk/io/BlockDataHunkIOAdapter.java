package com.volmit.iris.gen.v2.scaffold.hunk.io;

import com.volmit.iris.util.B;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockDataHunkIOAdapter extends PaletteHunkIOAdapter<BlockData> {

    @Override
    public void write(BlockData blockData, DataOutputStream dos) throws IOException {
        dos.writeUTF(blockData.getAsString(true));
    }

    @Override
    public BlockData read(DataInputStream din) throws IOException {
        return B.get(din.readUTF()).getBlockData();
    }
}