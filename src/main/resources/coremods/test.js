function initializeCoreMod() {
    return {
        'coremod_test_thingy': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': 'init',
                'methodDesc': '()V'
            },
            'transformer': function (method) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                var f_itemRenderer = ASMAPI.mapField('field_175621_X');

                var len = method.instructions.size();
                var putFieldIdx = 0;
                for (var i = 0; i < len; i++) {
                    var insn = method.instructions.get(i);
                    if (insn instanceof FieldInsnNode && insn.getOpcode() === Opcodes.PUTFIELD && insn.name === f_itemRenderer) {
                        putFieldIdx = i;
                        break;
                    }
                }
                var invokeSpecialInsn = method.instructions.get(putFieldIdx - 1);
                var newInsn = method.instructions.get(putFieldIdx - 9);
                invokeSpecialInsn.owner = 'codechicken/lib/render/item/CCRenderItem';
                newInsn.desc = 'codechicken/lib/render/item/CCRenderItem';
                return method;
            }
        }
    }
}
