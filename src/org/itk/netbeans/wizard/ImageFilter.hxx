#ifndef __itk${className}_hxx
#define __itk${className}_hxx

#include "${className}.h"

namespace itk
{
template<class TInputImage, class TOutputImage>
${className}<TInputImage, TOutputImage>::${className}()
{
   //TODO: initalize values
}

template<class TInputImage, class TOutputImage>
void
${className}<TInputImage, TOutputImage>::PrintSelf(std::ostream & os, Indent indent) const
{
   Superclass::PrintSelf(os, indent);
}

template<class TInputImage, class TOutputImage>
void
<#if multiThreaded>
${className}<TInputImage, TOutputImage>::ThreadedGenerateData(const OutputImageRegionType& outputRegion, ThreadIdType threadId)
<#else>
${className}<TInputImage, TOutputImage>::GenerateData()
</#if>
{
   InputImageConstPointer input = this->GetInput();
   OutputImagePointer output = this->GetOutput();
}
}

#endif /* __itk${className}_h */
