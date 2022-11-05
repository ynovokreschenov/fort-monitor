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
    createOrUpdateCompany(it)
    // второй уровень
    def children = it.children
    if (children){
        children.each{ item->
            createOrUpdateCompany(item)
            // группы / объекты 
            def childrenObjects = item.children
            if (childrenObjects){
                childrenObjects.each{ obj->
                    if (obj.leaf.toBoolean() == true){
                        createOrUpdateObject(obj, item)
                    } else {
                        def childrenNext = obj.children
                        if (childrenNext){
                            childrenNext.each{ itm ->
                                if (itm.leaf.toBoolean() == true){
                                    createOrUpdateObject(itm, item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

def createOrUpdateCompany(item){
    def fmId = item.real_id
    def companyParams = [
        'title':item.name,
        'parentId':item.parent_id,
        'removed':false
    ]
    def fmCompany = utils.findFirst('wAccount$fmAccount', ['fmId':fmId])
    if (!fmCompany){
        fmCompany = utils.create('wAccount$fmAccount', ['fmId':fmId]+companyParams)
    } else {
        utils.edit(fmCompany, companyParams)
    }
}

def createOrUpdateObject(obj, company){
    def fmId = obj.real_id
    def fmCompany = utils.findFirst('wAccount$fmAccount', ['fmId':company.real_id])
    def companyObj = fmCompany?.fmCompany
    if (companyObj){
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
            api.tx.call{
                fmObject = utils.create('ou$fmObject', ['fmId':fmId]+objectParams)
            }
        } else {
            api.tx.call{
                utils.edit(fmObject, objectParams)
            }
        }
    }
}