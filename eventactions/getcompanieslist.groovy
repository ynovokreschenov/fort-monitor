def sid = modules.fortmonitor.connect().result
def companyList = modules.fortmonitor.httpRequest(
  method = 'get',
  path = 'getcompanieslist',
  query = [:],
  body = [:],
  headers=[
    'SessionId':sid
  ]
).result.companies//.size()
modules.fortmonitor.disconnect()

companyList.each{
  def fmId = it.id
  def companyParams = [
    'title':it.name,
  ]
  def fmCompany = utils.findFirst('wAccount$fmAccount', ['fmId':fmId])
  if (!fmCompany){
    fmCompany = utils.create('wAccount$fmAccount', ['fmId':fmId]+companyParams)
  } else {
    utils.edit(fmCompany, companyParams)
  }
}