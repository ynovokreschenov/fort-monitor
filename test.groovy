def sid = modules.fortmonitor.connect().result
def companyList = modules.fortmonitor.httpRequest(
    method = 'get',
    path = 'gettree',
    query = ['all':true],
    body = [:],
    headers=[
        'SessionId':sid
    ]
).result.children
modules.fortmonitor.disconnect()

companyList.each{
    // первый уровень
    createOrUpdatefmAccount(it)
    // второй уровень
    def children = it.children
    if (children){
        children.each{ item->
            def fmAccount = createOrUpdatefmAccount(item)
            def companyObj = fmAccount.fmCompany
            if (companyObj){
                def beforeImportCompanyObjList = utils.find('ou$fmObject', ['parent':companyObj]) //, 'removed':false
                def importedCompanyObjList = []
                // группы / объекты
                def childrenObjects = item.children
                if (item.name == 'Добронравов'){
                    return childrenObjects
                }
                if (childrenObjects){
                    childrenObjects.each{ obj->
                        if (obj.leaf.toBoolean() == true){
                            //importedCompanyObjList.add(createOrUpdateObject(obj, companyObj))
                        } else {
                            def childrenNext = obj.children
                            if (childrenNext){
                                childrenNext.each{ itm ->
                                    if (itm.leaf.toBoolean() == true){
                                        //importedCompanyObjList.add(createOrUpdateObject(itm, companyObj))
                                    }
                                }
                            }
                        }
                    }
                }
                // заархивируем объекты, которых нет в импорте
                //def restObjects = beforeImportCompanyObjList-importedCompanyObjList
                //restObjects.each{ rmObj->
                //    utils.edit(rmObj, ['removed':true])
                //}
            }
        }
    }
}

def createOrUpdatefmAccount(item){
    def fmId = item.real_id
    def companyParams = [
        'title':item.name,
        'parentId':item.parent_id,
        'removed':false
    ]
    def fmAccount = utils.findFirst('wAccount$fmAccount', ['fmId':fmId])
    if (!fmAccount){
        fmAccount = utils.create('wAccount$fmAccount', ['fmId':fmId]+companyParams)
    } else {
        utils.edit(fmAccount, companyParams)
    }
    return fmAccount
}

def createOrUpdateObject(obj, companyObj){
    def fmId = obj.real_id
    def objectParams = [
        'title':obj.name,
        'fmImei':obj.IMEI,
        'fmIcon':obj.obj_icon,
        'fmRotateIcon':obj.obj_icon_rotate,
        'fmIconHeight':obj.obj_icon_height,
        'fmIconWidth':obj.obj_icon_width,
        'fmStatus':obj.status,
        'fmLat':obj.lat,
        'fmLon':obj.lon,
        'fmDirection':obj.direction,
        'fmMove':obj.move,
        'fmBlockReason':obj.block_reason,
        'removed':false,
        'parent':companyObj
    ]
    def fmObject = utils.findFirst('ou$fmObject', ['fmId':fmId])
    if (!fmObject){
        //api.tx.call{
            fmObject = utils.create('ou$fmObject', ['fmId':fmId]+objectParams)
        //}
    } else {
        //api.tx.call{
            utils.edit(fmObject, objectParams)
        //}
    }
    //}
    return fmObject
}