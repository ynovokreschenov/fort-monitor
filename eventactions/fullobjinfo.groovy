def fmObject = subject
def oid = fmObject.fmId.toString()
if (oid){
    def sid = modules.fortmonitor.connect().result
    def fullobjinfo = modules.fortmonitor.httpRequest(
      method = 'get',
      path = 'fullobjinfo',
      query = ['oid':oid],
      body = [:],
      headers = [
        'SessionId':sid
      ]
    )//.result
    modules.fortmonitor.disconnect()

    def param = [:]
    def fullobjinfoResult = fullobjinfo.result
    fullobjinfoResult.properties.each{
        if (it.name == 'Телефонный номер'){
            param['fmPhone'] = it.val
        }
    }
    if (fullobjinfoResult.block_reason != null){
      param['fmBlockReason'] = fullobjinfoResult.block_reason.toInteger()
    }
    if (param){
        def fmUpdateDate = new Date() + 1
        param['fmUpdateDate'] = fmUpdateDate
        utils.edit(fmObject, param)
    }
}