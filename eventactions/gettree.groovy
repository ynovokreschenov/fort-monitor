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
                def beforeImportCompanyObjList = utils.find('ou$fmObject', ['parent':companyObj, 'removed':false])
                def importedCompanyObjList = []
                // группы / объекты
                def childrenObjects = item.children
                if (childrenObjects){
                    childrenObjects.each{ obj_level1->
                        if (obj_level1.leaf.toBoolean() == true && obj_level1.IsGroup.toBoolean() == false){
                            importedCompanyObjList.add(createOrUpdateObject(obj_level1, companyObj))
                        } else {
                            def childrenLevel2 = obj_level1.children
                            if (childrenLevel2){
                                childrenLevel2.each{ obj_level2 ->
                                    if (obj_level2.leaf.toBoolean() == true && obj_level2.IsGroup.toBoolean() == false){
                                        importedCompanyObjList.add(createOrUpdateObject(obj_level2, companyObj))
                                    } else {
                                        def childrenLevel3 = obj_level2.children
                                        if (childrenLevel3){
                                            childrenLevel3.each{ obj_level3 ->
                                                if (obj_level3.leaf.toBoolean() == true && obj_level3.IsGroup.toBoolean() == false){
                                                    importedCompanyObjList.add(createOrUpdateObject(obj_level3, companyObj))
                                                } else {
                                                    def childrenLevel4 = obj_level3.children
                                                    if (childrenLevel4){
                                                        childrenLevel4.each{ obj_level4 ->
                                                            if (obj_level4.leaf.toBoolean() == true && obj_level4.IsGroup.toBoolean() == false){
                                                                importedCompanyObjList.add(createOrUpdateObject(obj_level4, companyObj))
                                                            }
                                                            else {
                                                                def childrenLevel5 = obj_level4.children
                                                                if (childrenLevel5){
                                                                    childrenLevel5.each{ obj_level5 ->
                                                                        if (obj_level5.leaf.toBoolean() == true && obj_level5.IsGroup.toBoolean() == false){
                                                                            importedCompanyObjList.add(createOrUpdateObject(obj_level5, companyObj))
                                                                        } else {
                                                                            def childrenLevel6 = obj_level5.children
                                                                            if (childrenLevel6){
                                                                                childrenLevel6.each{ obj_level6 ->
                                                                                    if (obj_level6.leaf.toBoolean() == true && obj_level6.IsGroup.toBoolean() == false){
                                                                                        importedCompanyObjList.add(createOrUpdateObject(obj_level6, companyObj))
                                                                                    } else {
                                                                                        def childrenLevel7 = obj_level6.children
                                                                                        if (childrenLevel7){
                                                                                            childrenLevel7.each{ obj_level7 ->
                                                                                                if (obj_level7.leaf.toBoolean() == true && obj_level7.IsGroup.toBoolean() == false){
                                                                                                    importedCompanyObjList.add(createOrUpdateObject(obj_level7, companyObj))
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // заархивируем объекты, которых нет в импорте
                def restObjects = beforeImportCompanyObjList-importedCompanyObjList
                restObjects.each{ rmObj->
                    utils.edit(rmObj, ['removed':true])
                }
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
    def fmObject = utils.findFirst('ou$fmObject', ['fmId':fmId, 'parent':companyObj])
    if (!fmObject){
        fmObject = utils.create('ou$fmObject', ['fmId':fmId]+objectParams)
    } else {
        api.tx.call{
            utils.edit(fmObject, objectParams)
        }
    }
    return fmObject
}