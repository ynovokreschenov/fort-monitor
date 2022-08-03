def fmObject = subject
def oid = fmObject.fmId.toString()
if (oid){
    def sid = modules.fortmonitor.connect().result
    def fullobjinfo = modules.fortmonitor.httpRequest('get', 'fullobjinfo', ['oid':oid], headers=['SessionId':sid])//.result
    modules.fortmonitor.disconnect()

    def param = [:]
    fullobjinfo.result.properties.each{
        if (it.name == 'Телефонный номер'){
            param['fmPhone'] = it.val
        }
    }
    if (param){
        def fmUpdateDate = new Date() + 1
        param['fmUpdateDate'] = fmUpdateDate
        utils.edit(fmObject, param)
    }
}