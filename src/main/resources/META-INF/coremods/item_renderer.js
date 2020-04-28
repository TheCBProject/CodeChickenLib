// 'Imports' Generated by, dumpJSImports.groovy
//@formatter:off
var ASMAPI                      = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Attribute                   = Java.type('org.objectweb.asm.Attribute');
var Handle                      = Java.type('org.objectweb.asm.Handle');
var Label                       = Java.type('org.objectweb.asm.Label');
var Opcodes                     = Java.type('org.objectweb.asm.Opcodes');
var Type                        = Java.type('org.objectweb.asm.Type');
var TypePath                    = Java.type('org.objectweb.asm.TypePath');
var TypeReference               = Java.type('org.objectweb.asm.TypeReference');
var AbstractInsnNode            = Java.type('org.objectweb.asm.tree.AbstractInsnNode');
var FieldInsnNode               = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var FieldNode                   = Java.type('org.objectweb.asm.tree.FieldNode');
var FrameNode                   = Java.type('org.objectweb.asm.tree.FrameNode');
var IincInsnNode                = Java.type('org.objectweb.asm.tree.IincInsnNode');
var InsnList                    = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode                    = Java.type('org.objectweb.asm.tree.InsnNode');
var IntInsnNode                 = Java.type('org.objectweb.asm.tree.IntInsnNode');
var InvokeDynamicInsnNode       = Java.type('org.objectweb.asm.tree.InvokeDynamicInsnNode');
var JumpInsnNode                = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode                   = Java.type('org.objectweb.asm.tree.LabelNode');
var LdcInsnNode                 = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var LineNumberNode              = Java.type('org.objectweb.asm.tree.LineNumberNode');
var LocalVariableAnnotationNode = Java.type('org.objectweb.asm.tree.LocalVariableAnnotationNode');
var LocalVariableNode           = Java.type('org.objectweb.asm.tree.LocalVariableNode');
var LookupSwitchInsnNode        = Java.type('org.objectweb.asm.tree.LookupSwitchInsnNode');
var MethodInsnNode              = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var MethodNode                  = Java.type('org.objectweb.asm.tree.MethodNode');
var MultiANewArrayInsnNode      = Java.type('org.objectweb.asm.tree.MultiANewArrayInsnNode');
var ParameterNode               = Java.type('org.objectweb.asm.tree.ParameterNode');
var TableSwitchInsnNode         = Java.type('org.objectweb.asm.tree.TableSwitchInsnNode');
var TryCatchBlockNode           = Java.type('org.objectweb.asm.tree.TryCatchBlockNode');
var TypeAnnotationNode          = Java.type('org.objectweb.asm.tree.TypeAnnotationNode');
var TypeInsnNode                = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var VarInsnNode                 = Java.type('org.objectweb.asm.tree.VarInsnNode');
//@formatter:on
function initializeCoreMod() {
    return {
        'IItemRenderer': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': '<init>',
                'methodDesc': '(Lnet/minecraft/client/GameConfiguration;)V'
            },
            'transformer': doTransform
        }
    }
}

var m_InvokeSpecial_ItemRenderer = remap({
    owner: 'net/minecraft/client/renderer/ItemRenderer',
    name: '<init>',
    desc: '(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/model/ModelManager;Lnet/minecraft/client/renderer/color/ItemColors;)V'
})

function doTransform(method) {
    var insn_InvokeSpecial_ItemRenderer = ASMAPI.findFirstMethodCall(
        method,
        ASMAPI.MethodType.SPECIAL,
        m_InvokeSpecial_ItemRenderer['owner'],
        m_InvokeSpecial_ItemRenderer['name'],
        m_InvokeSpecial_ItemRenderer['desc']
    );
    var insn_new_ItemRenderer = goBack(insn_InvokeSpecial_ItemRenderer, 8, true)
    insn_InvokeSpecial_ItemRenderer.owner = 'codechicken/lib/render/item/CCRenderItem'
    insn_new_ItemRenderer.desc = 'codechicken/lib/render/item/CCRenderItem'
    return method
}

//region from utils.js
function remap(mapping) {
    if (mapping['desc'].contains("(")) {
        mapping['name'] = ASMAPI.mapMethod(mapping['name']);
    } else {
        mapping['name'] = ASMAPI.mapField(mapping['name']);
    }
    return mapping;
}

function goBack(pointer, num, ignoreNonImportant) {
    for (var i = 0; i < num;) {
        pointer = pointer.getPrevious();
        if (ignoreNonImportant && isNonImportant(pointer)) {
            continue;
        }
        i++;
    }
    return pointer;
}

function isNonImportant(insn) {
    var type = insn.getType();
    return type == AbstractInsnNode.LINE || type == AbstractInsnNode.FRAME || type == AbstractInsnNode.LABEL
}
//endregion
