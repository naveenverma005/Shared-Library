#!/usr/bin env groovy
def executeAction(Map stepParams)
{
    dir("${stepParams.codePath}")
    {
        sh "packer ${stepParams.operation} -var-file=${stepParams.varfile} ${stepParams.packerfile}"
    }
}
