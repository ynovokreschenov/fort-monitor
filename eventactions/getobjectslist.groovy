def companyId = subject.fmAccount?.fmId
def sid = modules.fortmonitor.connect().result
def objectList = modules.fortmonitor.httpRequest('get', 'getobjectslist', ['companyId':companyId], headers=['SessionId':sid]).result.objects//.size()
modules.fortmonitor.disconnect()
//return objectList

objectList.each{
  def fmId = it.id
  def objectParams = [
    'title':it.name,
    'fmGroupId':it.groupId,
    'fmImei':it.IMEI,
    'fmIcon':it.fmIcon,
    'fmRotateIcon':it.rotateIcon,
    'fmIconHeight':it.iconHeight,
    'fmIconWidth':it.fmIconWidth,
    'fmStatus':it.status,
    'fmLat':it.lat,
    'fmLon':it.lon,
    'fmDirection':it.direction,
    'fmMove':it.move,
    'removed':false,
    'parent':subject
  ]
  //return 90
  def fmObject = utils.findFirst('ou$fmObject', ['fmId':fmId, 'parent':subject])
  //return fmObject
  if (!fmObject){
    fmObject = utils.create('ou$fmObject', ['fmId':fmId]+objectParams)
  } else {
    //return companyParams
    utils.edit(fmObject, objectParams)
  }
}